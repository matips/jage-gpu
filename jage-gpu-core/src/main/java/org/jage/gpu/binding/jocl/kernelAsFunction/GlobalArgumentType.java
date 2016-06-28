package org.jage.gpu.binding.jocl.kernelAsFunction;

import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;

/**
 * Define global argument witch can be bound to GPU by workplace
 */
public interface GlobalArgumentType<T> extends ArgumentType {
    /**
     *
     * @return block of openCL code witch will append to begging of kernel
     */
    String preExecutionBlock(String argumentName);

    void bind(JOCLKernelExecution joclKernelExecution, T value);
}
