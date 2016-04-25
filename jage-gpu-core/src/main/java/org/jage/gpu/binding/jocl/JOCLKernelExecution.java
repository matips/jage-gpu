package org.jage.gpu.binding.jocl;

import static org.jocl.CL.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.KernelExecution;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;

public class JOCLKernelExecution implements KernelExecution {
    private final cl_kernel kernel;
    private final cl_context context;
    private final cl_command_queue commandQueue;
    private final int elementsSize;
    private final long localWorkSize = Long.parseLong(System.getProperty("clLocalWorkSize", "0"));
    private final AtomicBoolean wasClose = new AtomicBoolean(false);
    /**
     * Bind out paramters (we will need to read it from GPU memory)
     */
    private final Map<KernelArgument, MemoryAndPointer> binded = new HashMap<>();

    @Override
    public void close() {
        if (!wasClose.getAndSet(true)) {
            binded.forEach((argument, memoryAndPointer) -> {
                        if (argument.isOut()) {
                            clEnqueueReadBuffer(commandQueue, memoryAndPointer.memory, CL_TRUE, 0,
                                    elementsSize * Sizeof.cl_double, memoryAndPointer.array,
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

        private MemoryAndPointer(cl_mem memory, Pointer array) {
            this.memory = memory;
            this.array = array;
        }

    }

    public JOCLKernelExecution(cl_kernel kernel, cl_context context, cl_command_queue commandQueue, int elementsSize) {
        this.kernel = kernel;
        this.context = context;
        this.commandQueue = commandQueue;
        this.elementsSize = elementsSize;
    }

    @Override
    public void execute() {
        long globalWorkSize = elementsSize + localWorkSize - (elementsSize % (localWorkSize == 0 ? 1 : localWorkSize));

        clSetKernelArg(kernel, 0, Sizeof.cl_int, Pointer.to(new long[] { globalWorkSize }));
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                new long[] { globalWorkSize }, localWorkSize != 0 ? new long[] { localWorkSize } : null,
                0, null, null);
        close();

    }

    @Override
    public void bindParameter(KernelArgument kernelArgument, double[] array) {
        assert array.length == elementsSize;
        bindParameter(kernelArgument, Pointer.to(array), Sizeof.cl_double);
    }

    @Override
    public void bindParameter(KernelArgument kernelArgument, int[] array) {
        assert array.length == elementsSize;
        bindParameter(kernelArgument, Pointer.to(array), Sizeof.cl_double);
    }

    private void bindParameter(KernelArgument kernelArgument, Pointer pointerToArray, int subtypeSize) {
        if (kernelArgument.isIn() && kernelArgument.isOut()) {
            bindInOutParameter(kernelArgument, pointerToArray, subtypeSize);
        } else if (kernelArgument.isIn()) {
            bindInParameter(kernelArgument, pointerToArray, subtypeSize);
        } else if (kernelArgument.isOut()) {
            bindOutParameter(kernelArgument, pointerToArray, subtypeSize);
        }
    }

    private void bindInParameter(KernelArgument kernelArgument, Pointer pointerToArray, int subtypeSize) {
        cl_mem mem = clCreateBuffer(context,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                subtypeSize * elementsSize, pointerToArray, null);
        clSetKernelArg(kernel, kernelArgument.getArgumentIndex(), Sizeof.cl_mem, Pointer.to(mem));
        binded.put(kernelArgument, new MemoryAndPointer(mem, pointerToArray));
    }

    private void bindOutParameter(KernelArgument kernelArgument, Pointer pointerToArray, int subtypeSize) {
        assert !binded.containsKey(kernelArgument);
        cl_mem mem = clCreateBuffer(context,
                CL_MEM_READ_WRITE,
                subtypeSize * elementsSize, null, null);
        clSetKernelArg(kernel, kernelArgument.getArgumentIndex(), Sizeof.cl_mem, Pointer.to(mem));
        binded.put(kernelArgument, new MemoryAndPointer(mem, pointerToArray));
    }

    private void bindInOutParameter(KernelArgument kernelArgument, Pointer pointerToArray, int subtypeSize) {
        assert !binded.containsKey(kernelArgument);
        cl_mem mem = clCreateBuffer(context,
                CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
                subtypeSize * elementsSize, kernelArgument.isIn() ? pointerToArray : null, null);
        clSetKernelArg(kernel, kernelArgument.getArgumentIndex(), Sizeof.cl_mem, Pointer.to(mem));
        binded.put(kernelArgument, new MemoryAndPointer(mem, pointerToArray));
    }
}

