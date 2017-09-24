package org.jage.gpu.binding.jocl.arguments;

import org.jage.gpu.binding.ArgumentAddressQualifier;
import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;

import java.util.List;
import java.util.Set;

public interface JoclArgumentType<T> extends ArgumentType {
    void bind(T var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument);

    List<String> getNames();

    Set<ArgumentAddressQualifier> validAddressSpaces();
}
