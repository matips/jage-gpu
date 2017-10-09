package org.jage.gpu.binding.jocl.arguments.arrays;

import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.JoclKernelArgument;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@JoclKernelArgument
public class FloatArray extends JoclArrayType<float[]> {
    public FloatArray() {
        super(float[].class, "float*");
    }

    @Override
    public void bind(float[] var, JOCLKernelExecution kernelExecution, org.jage.gpu.binding.KernelArgument kernelArgument) {
        kernelExecution.
                bindArrayParameter(kernelArgument, Pointer.to(var), var.length, Sizeof.cl_float);
    }
}
