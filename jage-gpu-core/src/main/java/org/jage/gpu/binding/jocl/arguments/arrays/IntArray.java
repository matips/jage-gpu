package org.jage.gpu.binding.jocl.arguments.arrays;

import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.JoclKernelArgument;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@JoclKernelArgument
public class IntArray extends JoclArrayType<int[]> {
    public IntArray() {
        super(int[].class, "uint*", "int*");
    }

    @Override
    public void bind(int[] var, JOCLKernelExecution kernelExecution, org.jage.gpu.binding.KernelArgument kernelArgument) {
        kernelExecution.bindArrayParameter(kernelArgument, Pointer.to(var), var.length, Sizeof.cl_int);
    }
}
