package org.jage.gpu.binding.jocl.arguments.arrays;

import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.JoclKernelArgument;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@JoclKernelArgument
public class CharArray extends JoclArrayType<char[]> {
    public CharArray() {
        super(char[].class, "char*");
    }

    @Override
    public void bind(char[] var, JOCLKernelExecution kernelExecution, org.jage.gpu.binding.KernelArgument kernelArgument) {
        kernelExecution.
                bindArrayParameter(kernelArgument, Pointer.to(var), var.length, Sizeof.cl_char);
    }
}
