package org.jage.gpu.binding;

public interface KernelExecution extends AutoCloseable {

    void execute();

    void bindParameter(KernelArgument kernelArgument, double[] array);

    @Override
    void close();
}
