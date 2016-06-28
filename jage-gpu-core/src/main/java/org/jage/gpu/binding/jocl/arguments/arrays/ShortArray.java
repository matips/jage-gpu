package org.jage.gpu.binding.jocl.arguments.arrays;

import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.GlobalArgument;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@GlobalArgument
public class ShortArray extends JoclArrayType<short[]> {
    public ShortArray() {
        super(short[].class, "short*");
    }

    @Override
    public void bind(short[] array, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        kernelExecution.bindArrayParameter(kernelArgument, Pointer.to(array), Sizeof.cl_short);
    }
}
