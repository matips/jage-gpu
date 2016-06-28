package org.jage.gpu.binding;

public interface KernelExecution extends AutoCloseable {

    void execute();

    @Override
    void close();

    <T>void bindParameter(KernelArgument globalArgument, T o);
}
