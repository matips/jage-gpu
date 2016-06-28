package org.jage.gpu.binding.jocl.arguments;

import java.util.Arrays;
import java.util.List;

import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;

public abstract class JoclArgumentType<T> implements ArgumentType {
    final List<String> names;
    final Class javaClass;

    protected JoclArgumentType(Class<T> javaClass, String... cNames) {
        if (isArray() ^ javaClass.isArray())
            throw new IllegalArgumentException(javaClass.toString() + (isArray() ? " is not an array" : "is array "));
        this.javaClass = javaClass;
        this.names = Arrays.asList(cNames);
    }

    @Override
    public String toString() {
        return names.get(0);
    }

    @Override
    public boolean is(Class javaType) {
        return this.javaClass.equals(javaType);
    }

    @Override
    public boolean isPointer() {
        return this.isArray(); //in c pointers and arrays are identical
    }

    public abstract void bind(T var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument);
}
