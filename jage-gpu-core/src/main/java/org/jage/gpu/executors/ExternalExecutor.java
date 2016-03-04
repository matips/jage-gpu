package org.jage.gpu.executors;

import org.jage.gpu.ExternalStepBuilder;

public interface ExternalExecutor {
    void flush();

    ExternalStepBuilder createStep();

}
