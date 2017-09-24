package org.jage.gpu.executors;

import org.jage.gpu.binding.jocl.AutoConfigGPU;
import org.jage.gpu.binding.jocl.kernelAsFunction.SimpleGPU;
import org.jage.gpu.helpers.ThrowingFunction;
import org.jage.gpu.helpers.Utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Allows to create gpuExecutor without explicit defining in/out arguments. Accepts multiple source files
 */
public class SimpleGpuExecutorRegistry implements ExternalExecutorRegistry {
    private Map<String, ExternalExecutor> executors = new HashMap<>();
    private final String allSources;
    private final AutoConfigGPU simpleGPU;

    public SimpleGpuExecutorRegistry(String sourceFiles) throws IOException {
        this(new String[]{
                sourceFiles}); //jAge xml configuration does not accept (String... sourceFiles) constructor as single String construction: this method is work around
    }

    public SimpleGpuExecutorRegistry(String... sourceFiles) throws IOException {
        this(new SimpleGPU(), sourceFiles);

    }

    public SimpleGpuExecutorRegistry(AutoConfigGPU simpleGPU, String... sourceFiles) {
        this.simpleGPU = simpleGPU;
        allSources = Arrays.stream(sourceFiles)
                .map((ThrowingFunction<String, String>) Utils::getResourceAsString)
                .collect(Collectors.joining("\n\n"));

    }

    public SimpleGpuExecutorRegistry(AutoConfigGPU simpleGPU, String sourceFile) {
        this(simpleGPU, new String[]{sourceFile});
    }

    @Override
    public ExternalExecutor get(String name) {
        return executors.computeIfAbsent(name, (ThrowingFunction<String, ExternalExecutor>) kernelName ->
                new GpuExecutor(
                        simpleGPU.buildKernel(allSources, name)
                )
        );
    }

    @Override
    public void flush() {
        executors.values().forEach(ExternalExecutor::flush);
    }
}

