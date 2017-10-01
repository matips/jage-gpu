package org.jage.gpu.binding.jocl.arguments.arrays;

import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.PrimitiveArgument;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@PrimitiveArgument
public class DoubleArray extends JoclArrayType<double[]> {
    public DoubleArray() {
        super(double[].class, "double*");
    }

    protected DoubleArray(String... cNames) {
        super(double[].class, cNames);
    }

    @Override
    public void bind(double[] var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        kernelExecution.bindArrayParameter(kernelArgument, Pointer.to(var), var.length, Sizeof.cl_double);
    }
}
