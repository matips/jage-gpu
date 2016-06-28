package org.jage.gpu.binding.jocl.arguments.arrays;

import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.GlobalArgument;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@GlobalArgument
public class FloatArray extends JoclArrayType<float[]> {
    public FloatArray() {
        super(float[].class, "float*");
    }

    @Override
    public void bind(float[] var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        kernelExecution.
                bindArrayParameter(kernelArgument, Pointer.to(var), Sizeof.cl_float);
    }
}
