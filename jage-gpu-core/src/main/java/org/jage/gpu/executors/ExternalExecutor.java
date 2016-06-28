package org.jage.gpu.executors;

import org.jage.gpu.ExternalStepBuilder;
import org.jage.gpu.binding.ArgumentType;

/**
 * Represents exteral executors like GPU. It allow to create new steps and flush all created and not flushed steps.
 */
public interface ExternalExecutor {
    /**
     * Calculate all created steps.
     */
    void flush();

    ExternalStepBuilder createStep();

    void bindGlobalArgument(ArgumentType type, Object argument);
}
