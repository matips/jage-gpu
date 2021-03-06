package org.jage.gpu.binding.jocl.arguments.primitives;

import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.JoclKernelArgument;
import org.jage.gpu.binding.jocl.arguments.DefaultJoclArgumentFactory;
import org.jage.gpu.binding.jocl.arguments.arrays.DoubleArray;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@JoclKernelArgument
public class Double extends JoclPrimitiveType<java.lang.Double> {
    Double() {
        super(java.lang.Double.class, "double");
    }

    @Override
    public void bind(java.lang.Double var, JOCLKernelExecution kernelExecution, org.jage.gpu.binding.KernelArgument kernelArgument) {
        kernelExecution.bindParameter(kernelArgument, Pointer.to(new double[] { var }), Sizeof.cl_double);
    }

    @Override
    public ArgumentType toArray() {
        return  DefaultJoclArgumentFactory.INSTANCE.fromClass(DoubleArray.class);
    }
}