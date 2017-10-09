package org.jage.gpu.binding.jocl.argumentAutoConfig;

import org.jage.gpu.binding.ArgumentAddressQualifier;
import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.JoclArgumentType;

import java.util.Set;

/**
 * Wraps JoclArgument to
 */
abstract class InOutWrapper<T> implements JoclArgumentType<T> {

    private final JoclArgumentType<T> joclArgumentType;

    public InOutWrapper(JoclArgumentType<T> joclArgumentType) {
        this.joclArgumentType = joclArgumentType;
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
    public void bind(T var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        joclArgumentType.bind(var, kernelExecution, kernelArgument);
    }

    @Override
    public boolean is(Class javaType) {
        return joclArgumentType.is(javaType);
    }


    @Override
    public Set<ArgumentAddressQualifier> validAddressSpaces() {
        return joclArgumentType.validAddressSpaces();
    }
}
