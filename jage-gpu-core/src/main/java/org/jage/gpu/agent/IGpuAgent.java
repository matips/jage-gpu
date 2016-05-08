package org.jage.gpu.agent;

import org.jage.gpu.executors.ExternalExecutor;
import org.jage.gpu.executors.ExternalExecutorRegistry;

public interface IGpuAgent {
    void initialize(ExternalExecutorRegistry externalExecutorRegistry);

    ExternalExecutor getGpuStep(String kernelName);
}
