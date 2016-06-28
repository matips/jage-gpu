package org.jage.gpu.binding.jocl.arguments.arrays;

import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.GlobalArgument;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@GlobalArgument
public class IntArray extends JoclArrayType<int[]> {
    public IntArray() {
        super(int[].class, "uint*", "int*");
    }

    @Override
    public void bind(int[] array, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        kernelExecution.bindArrayParameter(kernelArgument, Pointer.to(array), Sizeof.cl_int);
    }
}
