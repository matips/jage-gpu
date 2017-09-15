package org.jage.gpu.binding.jocl.kernelAsFunction.arguments;

import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.JoclArgumentType;

import java.util.List;

/**
 * Wraps primitive argument like int or int[]
 */
public class PrimitiveWrapper<T> implements FunctionArgumentType<T> {
    final JoclArgumentType<T> joclArgumentType;

    public PrimitiveWrapper(JoclArgumentType<T> joclArgumentType) {
        this.joclArgumentType = joclArgumentType;
    }

    @Override
    public void bind(T var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        joclArgumentType.bind(var, kernelExecution, kernelArgument);
    }

    @Override
    public List<String> getNames() {
        return joclArgumentType.getNames();
    }

    @Override
    public boolean isArray() {
        return joclArgumentType.isArray();
    }

    @Override
    public boolean isPointer() {
        return joclArgumentType.isPointer();
    }

    @Override
    public ArgumentType toArray() {
        return joclArgumentType.toArray();
    }

    @Override
    public boolean is(Class javaType) {
        return joclArgumentType.is(javaType);
    }

    @Override
    public String getCName() {
        return joclArgumentType.getCName();
    }

    @Override
    public String preExecutionBlock(KernelArgument argumentName) {
        return "";
    }

    @Override
    public ArgumentType toWrapperType() {
        return this;
    }

    @Override
    public String toString() {
        return joclArgumentType.toString();
    }

}
