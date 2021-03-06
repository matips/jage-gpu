package org.jage.gpu.binding.jocl.arguments.primitives;

import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.JoclKernelArgument;
import org.jage.gpu.binding.jocl.arguments.DefaultJoclArgumentFactory;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@JoclKernelArgument
public class Short extends JoclPrimitiveType<java.lang.Short> {
    Short() {
        super(java.lang.Short.class, "short", "ushort");
    }

    @Override
    public void bind(java.lang.Short var, JOCLKernelExecution kernelExecution, org.jage.gpu.binding.KernelArgument kernelArgument) {
        kernelExecution.bindParameter(kernelArgument, Pointer.to(new short[] { var }), Sizeof.cl_short);
    }

    @Override
    public ArgumentType toArray() {
        return DefaultJoclArgumentFactory.INSTANCE.fromClass(Short.class);
    }
}