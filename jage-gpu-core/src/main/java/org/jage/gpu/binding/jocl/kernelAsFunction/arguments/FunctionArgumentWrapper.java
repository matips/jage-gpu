package org.jage.gpu.binding.jocl.kernelAsFunction.arguments;

import org.jage.gpu.binding.ArgumentAddressQualifier;
import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.JoclArgumentType;

import java.util.List;
import java.util.Set;

/**
 * Wraps primitive argument like int or int[]
 */
public class FunctionArgumentWrapper<T> implements FunctionArgumentType<T> {
    final JoclArgumentType<T> joclArgumentType;

    public FunctionArgumentWrapper(JoclArgumentType<T> joclArgumentType) {
        this.joclArgumentType = joclArgumentType;
    }

    @Override
    public void bind(T var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        joclArgumentType.bind(var, kernelExecution, kernelArgument);
    }

    @Override
    public Set<ArgumentAddressQualifier> validAddressSpaces() {
        return joclArgumentType.validAddressSpaces();
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
    public List<String> getCNames() {
        return joclArgumentType.getCNames();
    }

    @Override
    public String preExecutionBlock(KernelArgument argumentName) {
        return "";
    }

    @Override
    public String toSubname(KernelArgument kernelArgument) {
        return kernelArgument.getArgumentName();
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
