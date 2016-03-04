package org.jage.gpu.executors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jage.gpu.binding.Kernel;

public class GpuExecutorRegistry implements ExternalExecutorRegistry {
    Map<String, ExternalExecutor> executors = new HashMap<>();
    private List<Kernel> kernels;

    public GpuExecutorRegistry() {
    }

    public GpuExecutorRegistry(List<Kernel> kernels) {
        setKernels(kernels);
    }

    public void setKernels(List<Kernel> kernels) {
        this.kernels = kernels;
    }

    @Override
    public ExternalExecutor get(String name) {
        return executors.computeIfAbsent(name, kernelName ->
                new GpuExecutor(
                        kernels.stream().filter(
                                kernel -> kernel
                                        .getKernelName().equals(kernelName)
                        ).findAny().orElseThrow(
                                () -> new RuntimeException("Cannot find " + kernelName + " in registry")
                        )
                )
        );
    }

    @Override
    public void flush() {
        executors.values().forEach(ExternalExecutor::flush);
    }
}
