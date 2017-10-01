package org.jage.gpu.binding.jocl;

import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.KernelExecution;
import org.jage.gpu.binding.jocl.arguments.JoclArgumentFactory;
import org.jage.gpu.binding.jocl.arguments.JoclArgumentType;
import org.jocl.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.jocl.CL.*;

public class JOCLKernelExecution implements KernelExecution {
    private final cl_kernel kernel;
    private final cl_context context;
    private final cl_command_queue commandQueue;
    private final int globalWorkers;
    private final long localWorkSize = Long.parseLong(System.getProperty("clLocalWorkSize", "0"));
    private final AtomicBoolean wasClose = new AtomicBoolean(false);
    /**
     * Bind out parameters (we will need to read it from GPU memory)
     */
    private final Map<KernelArgument, MemoryAndPointer> binded = new HashMap<>();
    private final JoclArgumentFactory argumentFactory;

    @Override
    public void close() {
        if (!wasClose.getAndSet(true)) {
            binded.forEach((argument, memoryAndPointer) -> {
                        if (argument.isOut()) {
                            clEnqueueReadBuffer(commandQueue, memoryAndPointer.memory, CL_TRUE, 0,
                                    memoryAndPointer.arraySize * memoryAndPointer.size, memoryAndPointer.array,
                                    0, null, null);
                        }
                        clReleaseMemObject(memoryAndPointer.memory);
                    }
            );
        }
    }

    private class MemoryAndPointer {
        final cl_mem memory;
        final Pointer array;
        final int size;
        private final int arraySize;

        private MemoryAndPointer(cl_mem memory, Pointer array, int arraySize, int size) {
            this.memory = memory;
            this.array = array;
            this.arraySize = arraySize;
            this.size = size;
        }

    }

    public JOCLKernelExecution(cl_kernel kernel, cl_context context, cl_command_queue commandQueue, int globalWorkers, JoclArgumentFactory argumentFactory) {
        this.kernel = kernel;
        this.context = context;
        this.commandQueue = commandQueue;
        this.globalWorkers = globalWorkers;
        this.argumentFactory = argumentFactory;

    }

    @Override
    public void execute() {
        long height = nextPowerOf2(this.globalWorkers);
        long globalWorkSize = height + localWorkSize - (height % (localWorkSize == 0 ? 1 : localWorkSize));

        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                new long[] { globalWorkSize }, localWorkSize != 0 ? new long[] { localWorkSize } : null,
                0, null, null);
        close();

    }
    private static long nextPowerOf2(final long a)
    {
        int b = 1;
        while (b < a)
        {
            b = b << 1;
        }
        return b;
    }
    @Override
    public <T> void bindParameter(KernelArgument globalArgument, T o) {
        if (!globalArgument.getType().is(o.getClass())) {
            throw new RuntimeException("Invalid type " + o.getClass().getName() + " bind for " + globalArgument.getType());
        }
        ((JoclArgumentType<T>) globalArgument.getType()).bind(o, this, globalArgument);
    }

    private int bindParameter(int arg_index, int typeSize, Pointer to) {
        return clSetKernelArg(kernel, arg_index, typeSize, to);
    }

    public int bindParameter(KernelArgument kernelArgument, Pointer to, int typeSize) {
        return clSetKernelArg(kernel, kernelArgument.getArgumentIndex(), typeSize, to);
    }

    public void bindArrayParameter(KernelArgument kernelArgument, Pointer pointerToArray, int arraySize, int subtypeSize) {
        if (kernelArgument.isIn() && kernelArgument.isOut()) {
            bindInOutParameter(kernelArgument, pointerToArray, arraySize, subtypeSize);
        } else if (kernelArgument.isIn()) {
            bindInParameter(kernelArgument, pointerToArray, arraySize, subtypeSize);
        } else if (kernelArgument.isOut()) {
            bindOutParameter(kernelArgument, pointerToArray, arraySize, subtypeSize);
        }
    }

    private void bindInParameter(KernelArgument kernelArgument, Pointer pointerToArray, int arraySize, int subtypeSize) {
        cl_mem mem = clCreateBuffer(context,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                subtypeSize * arraySize, pointerToArray, null);
        bindParameter(kernelArgument.getArgumentIndex(), Sizeof.cl_mem, Pointer.to(mem));
        binded.put(kernelArgument, new MemoryAndPointer(mem, pointerToArray, arraySize, subtypeSize));
    }

    private void bindOutParameter(KernelArgument kernelArgument, Pointer pointerToArray, int arraySize, int subtypeSize) {
        assert !binded.containsKey(kernelArgument);
        cl_mem mem = clCreateBuffer(context,
                CL_MEM_READ_WRITE,
                subtypeSize * arraySize, null, null);
        bindParameter(kernelArgument.getArgumentIndex(), Sizeof.cl_mem, Pointer.to(mem));
        binded.put(kernelArgument, new MemoryAndPointer(mem, pointerToArray, arraySize, subtypeSize));
    }

    private void bindInOutParameter(KernelArgument kernelArgument, Pointer pointerToArray, int arraySize, int subtypeSize) {
        assert !binded.containsKey(kernelArgument);
        cl_mem mem = clCreateBuffer(context,
                CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
                subtypeSize * arraySize, kernelArgument.isIn() ? pointerToArray : null, null);
        bindParameter(kernelArgument.getArgumentIndex(), Sizeof.cl_mem, Pointer.to(mem));
        binded.put(kernelArgument, new MemoryAndPointer(mem, pointerToArray, arraySize, subtypeSize));
    }
}

