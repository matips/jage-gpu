package org.jage.gpu.binding.jocl.arguments.arrays;

import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.JoclKernelArgument;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@JoclKernelArgument
public class DoubleArray extends JoclArrayType<double[]> {
    public DoubleArray() {
        super(double[].class, "double*");
    }

    protected DoubleArray(String... cNames) {
        super(double[].class, cNames);
    }

    @Override
    public void bind(double[] var, JOCLKernelExecution kernelExecution, org.jage.gpu.binding.KernelArgument kernelArgument) {
        kernelExecution.bindArrayParameter(kernelArgument, Pointer.to(var), var.length, Sizeof.cl_double);
    }
}
