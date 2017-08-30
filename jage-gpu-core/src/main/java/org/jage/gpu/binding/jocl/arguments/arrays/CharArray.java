package org.jage.gpu.binding.jocl.arguments.arrays;

import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.PrimitiveArgument;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@PrimitiveArgument
public class CharArray extends JoclArrayType<char[]> {
    public CharArray() {
        super(char[].class, "char*");
    }

    @Override
    public void bind(char[] var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        kernelExecution.
                bindArrayParameter(kernelArgument, Pointer.to(var), Sizeof.cl_char);
    }
}
