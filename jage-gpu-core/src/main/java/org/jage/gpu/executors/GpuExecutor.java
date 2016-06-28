package org.jage.gpu.executors;

import java.util.concurrent.atomic.AtomicReference;

import org.jage.gpu.ExternalStepBuilder;
import org.jage.gpu.binding.Kernel;

public class GpuExecutor implements ExternalExecutor {

    private final Kernel kernel;
    private final AtomicReference<GpuExecution> executionToFlush;

    public GpuExecutor(Kernel kernel) {
        this.kernel = kernel;
        this.executionToFlush = new AtomicReference<>(new GpuExecution(kernel.getArguments()));
    }

    @Override
    public void flush() {
        executionToFlush.getAndSet(new GpuExecution(kernel.getArguments()))
                .flush(kernel);
    }

    @Override
    public ExternalStepBuilder createStep() {
        return executionToFlush.get().getStepBuilder();
    }

    @Override
    public void bindGlobalArgument(Object argument) {
        executionToFlush.get().bindGlobalArgument(argument);
    }
}
