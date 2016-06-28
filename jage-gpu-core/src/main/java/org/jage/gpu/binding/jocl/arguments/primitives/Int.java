package org.jage.gpu.binding.jocl.arguments.primitives;

import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.GlobalArgument;
import org.jage.gpu.binding.jocl.arguments.JoclArgumentFactory;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@GlobalArgument
public class Int extends JoclPrimitiveType<Integer> {
    Int() {
        super(Integer.class, "uint", "int");
    }

    @Override
    public void bind(Integer var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        kernelExecution.bindParameter(kernelArgument, Pointer.to(new long[] { var }), Sizeof.cl_long);
    }

    @Override
    public ArgumentType toArray() {
        return JoclArgumentFactory.fromClass(int[].class);
    }
}