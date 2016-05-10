package org.jage.gpu.binding.jocl;

import static org.jocl.CL.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jage.gpu.binding.ArgumentAccessQualifier;
import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.KernelExecution;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_kernel;

/**
 * Basic Kernel implementation. Expect user to configure witch arguments are in and witch are out arguments.
 */
public class JOCLKernel implements Kernel {
    private static final Logger LOGGER = Logger.getLogger(JOCLKernel.class);
    private final cl_kernel kernel;
    private final JoclGpu gpu;
    private final String kernelName;
    private List<KernelArgument> arguments;

    public JOCLKernel(JoclGpu gpu, cl_kernel kernel, String kernelName, Set<String> inArguments, Set<String> outArguments) throws IOException {
        this.kernel = kernel;
        this.kernelName = kernelName;
        this.gpu = gpu;

        loadKernelInfo(inArguments, outArguments);
    }

    private void loadKernelInfo(Set<String> inArguments, Set<String> outArguments) {
        LOGGER.info("Loading information about kernel: " + kernelName);
        // Arrays that will store the parameter values
        int numArgs = getNumArgs();

        // Obtain information about each argument
        ArrayList<KernelArgument> argumentsTemp = new ArrayList<>(numArgs);
        for (int paramNumber = 0; paramNumber < numArgs; paramNumber++) {
            String argumentName = getArgumentName(paramNumber);
            ArgumentAccessQualifier accessQualifier = getArgumentAccessQualifierCode(paramNumber);
            ArgumentType typeName = getArgumentType(paramNumber);
            boolean isIn = inArguments.contains(argumentName);
            boolean isOut = outArguments.contains(argumentName);
            LOGGER.info("Argument " + argumentName + " isIn = " + isIn);
            LOGGER.info("Argument " + argumentName + " isOut = " + isIn);

            argumentsTemp.add(new KernelArgument(paramNumber, argumentName, accessQualifier, typeName, isIn, isOut));
        }
        arguments = Collections.unmodifiableList(argumentsTemp);
    }

    private int getNumArgs() {
        int paramValueInt[] = { 0 };
        clGetKernelInfo(kernel, CL_KERNEL_NUM_ARGS, Sizeof.cl_uint, Pointer.to(paramValueInt), null);
        return paramValueInt[0];
    }

    private ArgumentAccessQualifier getArgumentAccessQualifierCode(int argumentNumber) {
        int paramValueInt[] = { 0 };
        clGetKernelArgInfo(kernel, argumentNumber, CL_KERNEL_ARG_ACCESS_QUALIFIER, Sizeof.cl_int, Pointer.to(paramValueInt), null);
        int accessQualifierCode = paramValueInt[0];
        LOGGER.info(String.format("%d kernel %s argument has access qualifier: %d", argumentNumber, kernelName, accessQualifierCode));
        return ArgumentAccessQualifier.fromCode(accessQualifierCode);
    }

    private ArgumentType getArgumentType(int argumentNumber) {
        long sizeArray[] = { 0 };
        byte paramValueCharArray[] = new byte[1024];
        clGetKernelArgInfo(kernel, argumentNumber, CL_KERNEL_ARG_TYPE_NAME, 0, null, sizeArray);
        clGetKernelArgInfo(kernel, argumentNumber, CL_KERNEL_ARG_TYPE_NAME, sizeArray[0], Pointer.to(paramValueCharArray), null);
        String typeName = new String(paramValueCharArray, 0, (int) sizeArray[0] - 1);
        LOGGER.info(String.format("%d kernel %s argument has type: %s", argumentNumber, kernelName, typeName));
        return ArgumentType.fromName(typeName);
    }

    private String getArgumentName(int argumentNumber) {
        long sizeArray[] = { 0 };
        byte paramValueCharArray[] = new byte[1024];
        clGetKernelArgInfo(kernel, argumentNumber, CL_KERNEL_ARG_NAME, 0, null, sizeArray);
        clGetKernelArgInfo(kernel, argumentNumber, CL_KERNEL_ARG_NAME, sizeArray[0], Pointer.to(paramValueCharArray), null);
        return new String(paramValueCharArray, 0, (int) sizeArray[0] - 1);
    }

    @Override
    public String getKernelName() {
        return kernelName;
    }

    @Override
    public List<KernelArgument> getArguments() {
        return arguments;
    }

    @Override
    public KernelExecution newExecution(int elementsSize) {
        return new JOCLKernelExecution(kernel, gpu.getContext(), gpu.getCommandQueue(), elementsSize);
    }
}

