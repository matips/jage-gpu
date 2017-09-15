package org.jage.gpu.binding.jocl.kernelAsFunction.arguments;

import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.arguments.JoclArgumentType;

import java.util.Collection;

/**
 * Define global argument witch can be bound to GPU by workplace
 */
public interface FunctionArgumentType<T> extends JoclArgumentType<T> {
    String preExecutionBlock(KernelArgument argumentName);

    ArgumentType toWrapperType();
}
