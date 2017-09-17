package org.jage.gpu.binding.jocl.kernelAsFunction.arguments;

import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.arguments.JoclArgumentType;

import java.util.Collection;

/**
 * Define global argument witch can be bound to GPU by workplace
 */
public interface FunctionArgumentType<T> extends JoclArgumentType<T> {
    /**
     * When function is convert to kernel it allow to add some "global" code (before each agent execution part)
     * @param argumentName
     * @return
     */
    String preExecutionBlock(KernelArgument argumentName);

    /**
     * If preExection block changed name of variable this function return new name;
     * @param kernelArgument
     * @return
     */
    default String toSubname(KernelArgument kernelArgument){
        return kernelArgument.getArgumentName();
    }
    ArgumentType toWrapperType();
}
