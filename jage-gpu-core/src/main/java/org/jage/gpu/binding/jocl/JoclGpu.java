
package org.jage.gpu.binding.jocl;

import static org.jocl.CL.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jage.gpu.binding.GPU;
import org.jage.gpu.binding.Kernel;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;

/**
 * A sample showing a simple reduction with JOCL
 */
public class JoclGpu implements GPU {
    private static final Logger LOGGER = Logger.getLogger(JoclGpu.class);
    private cl_context context;
    private AtomicBoolean wasShutdowned = new AtomicBoolean(false);
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    /**
     * The OpenCL command queue to which the all work will be dispatched
     */
    private cl_command_queue commandQueue;
    private cl_device_id device;
    private List<cl_kernel> createdKernels = new ArrayList<>();
    private List<cl_program> createdPrograms = new ArrayList<>();

    /**
     *
     * @param initalize if true GPU is initialized in constructor
     * @throws IOException
     */
    public JoclGpu(boolean initalize) throws IOException {
        if (initalize)
            this.initialize();
    }

    public JoclGpu() {
    }

    /**
     * Initialize a default OpenCL context, command queue, program and kernel
     */
    public void initialize() throws IOException {
        try {
            writeLock.lock();
            final int platformIndex = Integer.parseInt(System.getProperty("JOCLPlatformIndex", "0"));
            final long deviceType = CL_DEVICE_TYPE_ALL;
            final int deviceIndex = Integer.parseInt(System.getProperty("JOCLDeviceIndex", "0"));
            LOGGER.info("Device index: " + deviceIndex);
            LOGGER.info("Platform index: " + platformIndex);

            CL.setExceptionsEnabled(true);

            // Obtain the number of platforms
            int numPlatformsArray[] = new int[1];
            clGetPlatformIDs(0, null, numPlatformsArray);
            int numPlatforms = numPlatformsArray[0];
            LOGGER.info("Number of platforms: " + numPlatforms);

            // Obtain a platform ID
            cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
            clGetPlatformIDs(platforms.length, platforms, null);
            cl_platform_id platform = platforms[platformIndex];

            // Check if the platform supports OpenCL 1.2
            long sizeArray[] = { 0 };
            clGetPlatformInfo(platform, CL_PLATFORM_VERSION, 0, null, sizeArray);
            byte buffer[] = new byte[(int) sizeArray[0]];
            clGetPlatformInfo(platform, CL_PLATFORM_VERSION,
                    buffer.length, Pointer.to(buffer), null);
            String versionString = new String(buffer, 0, buffer.length - 1);
            LOGGER.info("Platform version: " + versionString);
            String versionNumberString = versionString.substring(7, 10);
            try {
                String majorString = versionNumberString.substring(0, 1);
                String minorString = versionNumberString.substring(2, 3);
                int major = Integer.parseInt(majorString);
                int minor = Integer.parseInt(minorString);
                if (major == 1 && minor < 2) {
                    LOGGER.fatal("Platform only supports OpenCL " + versionNumberString);
                    throw new RuntimeException("Platform only supports OpenCL " + versionNumberString);
                }
            } catch (NumberFormatException e) {
                LOGGER.fatal("Invalid version number: " + versionNumberString);
                throw new RuntimeException("Invalid version number: " + versionNumberString);
            }

            // Initialize the context properties
            cl_context_properties contextProperties = new cl_context_properties();
            contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

            // Obtain the number of devices for the platform
            int numDevicesArray[] = new int[1];
            clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
            int numDevices = numDevicesArray[0];

            LOGGER.info("Number of devices: " + numDevices);
            // Obtain a device ID
            cl_device_id devices[] = new cl_device_id[numDevices];
            clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
            device = devices[deviceIndex];

            // Create a context for the selected device
            context = clCreateContext(
                    contextProperties, 1, new cl_device_id[] { device },
                    null, null, null);

            commandQueue = clCreateCommandQueue(context, device, 0, null);

            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        } finally {
            writeLock.unlock();
        }

    }

    @Override
    public JOCLKernel buildKernel(String kernelFileContent, String kernelName, Set<String> inArguments, Set<String> outArguments) throws IOException {
        try {
            readLock.lock();
            if (wasShutdowned.get()) {
                throw new IllegalStateException("GPU was shutdown, cannot execute operations");
            }
            int[] errorCode = new int[1];
            cl_program program = clCreateProgramWithSource(context, 1, new String[] { kernelFileContent }, null, errorCode);
            checkCreateProgramError(errorCode[0], kernelName);
            this.createdPrograms.add(program);

            // Build the program
            clBuildProgram(program, 0, null, "-cl-kernel-arg-info", null, null);

            // Create the kernel
            cl_kernel kernel = clCreateKernel(program, kernelName, null);
            this.createdKernels.add(kernel);

            return new JOCLKernel(this, kernel, kernelName, inArguments, outArguments);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Kernel buildKernel(File sourceFile, String kernelName, Set<String> inArguments, Set<String> outArguments) throws IOException {
        // Create the program from the source code
        String programSource = FileUtils.readFileToString(sourceFile);
        return buildKernel(programSource, kernelName, inArguments, outArguments);

    }

    //todo: extract to helper class
    private void checkCreateProgramError(int errorCode, String name) {
        switch (errorCode) {
        case 0:
            return;
        case CL_INVALID_CONTEXT:
            throw new RuntimeException("Error during creating program " + name + " error: CL_INVALID_CONTEXT");
        case CL_INVALID_VALUE:
            throw new RuntimeException("Error during creating program " + name + " error: CL_INVALID_VALUE");
        case CL_OUT_OF_HOST_MEMORY:
            throw new RuntimeException("Error during creating program " + name + " error: CL_OUT_OF_HOST_MEMORY");
        }

    }

    public void shutdown() {
        try {
            writeLock.lock();
            if (!wasShutdowned.getAndSet(true)) {
                createdKernels.forEach(CL::clReleaseKernel);
                createdPrograms.forEach(CL::clReleaseProgram);
                clReleaseCommandQueue(commandQueue);
                clReleaseContext(context);
            }
        } finally {
            writeLock.unlock();

        }
    }

    cl_context getContext() {
        return context;

    }

    cl_command_queue getCommandQueue() {
        return commandQueue;
    }
}