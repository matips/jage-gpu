package org.jage.gpu;

import org.jage.gpu.agent.GpuReader;

@FunctionalInterface
public interface KernelCallBack {
    void execute(GpuReader gpuReader);
}
