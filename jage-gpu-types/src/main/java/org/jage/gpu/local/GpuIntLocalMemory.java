package org.jage.gpu.local;

import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.FunctionArgumentType;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.GlobalArgument;
import org.jocl.Sizeof;

import java.util.Collections;
import java.util.List;

/**
 * CRIF IT Solutions Poland
 */
@GlobalArgument
class GpuIntLocalMemory implements FunctionArgumentType<Integer> {

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isPointer() {
        return false;
    }

    @Override
    public ArgumentType toArray() {
        throw new RuntimeException("OpenCL does not supports pointers of pointers.");
    }

    @Override
    public boolean is(Class javaType) {
        return Integer.class.equals(javaType);
    }

    @Override
    public String getCName() {
        return "LocalIntArray";
    }

    @Override
    public void bind(Integer var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        kernelExecution.bindParameter(kernelArgument, null, (Sizeof.cl_int) * var);
    }

    @Override
    public List<String> getNames() {
        return Collections.singletonList("LocalIntArray");
    }

    @Override
    public String preExecutionBlock(KernelArgument argumentName) {
        return "";
    }

    @Override
    public ArgumentType toWrapperType() {
        return this;
    }
}
