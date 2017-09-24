package org.jage.gpu.binding.jocl.arguments.arrays;

import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.PrimitiveArgument;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@PrimitiveArgument
public class ShortArray extends JoclArrayType<short[]> {
    public ShortArray() {
        super(short[].class, "short*");
    }

    @Override
    public void bind(short[] var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        kernelExecution.bindArrayParameter(kernelArgument, Pointer.to(var), var.length, Sizeof.cl_short);
    }
}
