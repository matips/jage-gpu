package org.jage.gpu.binding;

public interface KernelExecution extends AutoCloseable {

    void execute();

    void bindParameter(KernelArgument kernelArgument, double[] array);

    void bindParameter(KernelArgument kernelArgument, int[] array);

    @Override
    void close();
}
