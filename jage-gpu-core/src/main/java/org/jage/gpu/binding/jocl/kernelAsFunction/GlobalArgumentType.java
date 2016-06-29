package org.jage.gpu.binding.jocl.kernelAsFunction;

import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.jocl.arguments.JoclArgumentType;

/**
 * Define global argument witch can be bound to GPU by workplace
 */
public abstract class GlobalArgumentType<T> extends JoclArgumentType {
    protected GlobalArgumentType(Class javaClass, String... cNames) {
        super(javaClass, cNames);
    }

    /**
     * @return block of openCL code witch will append to begging of kernel
     */
    abstract String preExecutionBlock(String argumentName);

    public ArgumentType toWrapperType() {
        return this;
    }
}
