package org.jage.gpu.random;

import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.FunctionArgumentType;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.GlobalArgument;
import org.jocl.Pointer;
import org.jocl.Sizeof;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@GlobalArgument
class GpuRandomType implements FunctionArgumentType<Random> {
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
        return Random.class.equals(javaType);
    }

    @Override
    public String getCName() {
        return "Random";
    }

    @Override
    public void bind(Random var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        kernelExecution.bindParameter(kernelArgument, Pointer.to(new long[]{var.nextLong()}), Sizeof.cl_long);
    }

    @Override
    public List<String> getNames() {
        return Collections.singletonList("Random");
    }

    @Override
    public String preExecutionBlock(KernelArgument argumentName) {
        return "<PRE>";
    }

    @Override
    public ArgumentType toWrapperType() {
        return this;
    }
}