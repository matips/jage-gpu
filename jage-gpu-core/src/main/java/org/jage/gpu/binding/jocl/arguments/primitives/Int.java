package org.jage.gpu.binding.jocl.arguments.primitives;

import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.JoclKernelArgument;
import org.jage.gpu.binding.jocl.arguments.DefaultJoclArgumentFactory;
import org.jage.gpu.binding.jocl.arguments.arrays.IntArray;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@JoclKernelArgument
public class Int extends JoclPrimitiveType<Integer> {
    protected Int() {
        super(Integer.class, "uint", "int");
    }

    @Override
    public void bind(Integer var, JOCLKernelExecution kernelExecution, org.jage.gpu.binding.KernelArgument kernelArgument) {
        kernelExecution.bindParameter(kernelArgument, Pointer.to(new int[] { var }), Sizeof.cl_uint);
    }

    @Override
    public ArgumentType toArray() {
        return DefaultJoclArgumentFactory.INSTANCE.fromClass(IntArray.class);
    }
}