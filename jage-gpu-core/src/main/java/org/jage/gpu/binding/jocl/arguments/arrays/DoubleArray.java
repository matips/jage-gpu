package org.jage.gpu.binding.jocl.arguments.arrays;

import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.GlobalArgument;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@GlobalArgument
public class DoubleArray extends JoclArrayType<double[]> {
    public DoubleArray() {
        super(double[].class, "double*");
    }

    @Override
    public void bind(double[] var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        kernelExecution.bindArrayParameter(kernelArgument, Pointer.to(var), Sizeof.cl_double);
    }
}
