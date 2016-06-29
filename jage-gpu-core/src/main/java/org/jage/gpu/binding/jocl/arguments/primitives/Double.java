package org.jage.gpu.binding.jocl.arguments.primitives;

import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.GlobalArgument;
import org.jage.gpu.binding.jocl.arguments.DefaultJoclArgumentFactory;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@GlobalArgument
public class Double extends JoclPrimitiveType<java.lang.Double> {
    Double() {
        super(java.lang.Double.class, "double");
    }

    @Override
    public void bind(java.lang.Double var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        kernelExecution.bindParameter(kernelArgument, Pointer.to(new double[] { var }), Sizeof.cl_double);
    }

    @Override
    public ArgumentType toArray() {
        return  DefaultJoclArgumentFactory.INSTANCE.fromClass(double[].class);
    }
}