package org.jage.gpu.binding.jocl.arguments.arrays;

import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.PrimitiveArgument;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@PrimitiveArgument
public class IntArray extends JoclArrayType<int[]> {
    public IntArray() {
        super(int[].class, "uint*", "int*");
    }

    @Override
    public void bind(int[] var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        kernelExecution.bindArrayParameter(kernelArgument, Pointer.to(var), var.length, Sizeof.cl_int);
    }
}
