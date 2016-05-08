package org.jage.gpu.executors;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.jage.gpu.binding.jocl.kernelAsFunction.SimpleGPU;
import org.jage.gpu.helpers.ThrowingFunction;

/**
 * Allows to create gpuExecutor without explicit defining in/out arguments. Accepts multiple source files
 */
public class SimpleGpuExecutorRegistry implements ExternalExecutorRegistry {
    private Map<String, ExternalExecutor> executors = new HashMap<>();
    private final String allSources;
    private final SimpleGPU simpleGPU;

    public SimpleGpuExecutorRegistry(String... sourceFiles) throws IOException {
        simpleGPU = new SimpleGPU();
        allSources = Arrays.stream(sourceFiles)
                .map(ClassLoader::getSystemResource)
                .map(URL::getFile)
                .map(File::new)
                .map(file -> {
                    try {
                        return FileUtils.readFileToString(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.joining("\n\n"));

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

