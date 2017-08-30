package org.jage.gpu.binding.jocl.arguments;

import java.util.List;

import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;

public interface JoclArgumentType<T> extends ArgumentType {
    void bind(T var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument);

    List<String> getNames();
}
