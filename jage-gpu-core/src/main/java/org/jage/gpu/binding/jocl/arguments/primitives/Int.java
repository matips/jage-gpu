package org.jage.gpu.binding.jocl.arguments.primitives;

import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.PrimitiveArgument;
import org.jage.gpu.binding.jocl.arguments.DefaultJoclArgumentFactory;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@PrimitiveArgument
public class Int extends JoclPrimitiveType<Integer> {
    Int() {
        super(Integer.class, "uint", "int");
    }

    @Override
    public void bind(Integer var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        kernelExecution.bindParameter(kernelArgument, Pointer.to(new int[] { var }), Sizeof.cl_uint);
    }

    @Override
    public ArgumentType toArray() {
        return DefaultJoclArgumentFactory.INSTANCE.fromClass(int[].class);
    }
}