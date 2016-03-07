package org.jage.gpu.executors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jage.gpu.ExternalStepBuilder;
import org.jage.gpu.KernelCallBack;
import org.jage.gpu.agent.GpuReader;
import org.jage.gpu.agent.SubStep;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.KernelExecution;
import org.jage.gpu.executors.arguments.DoubleArguments;
import org.jage.gpu.executors.arguments.IntArguments;

class GpuExecution {
    private final DoubleArguments doubleArguments;
    private final AtomicInteger argumentsRowsIndex = new AtomicInteger();
    private final ArrayList<KernelCallBack> callBacks = new ArrayList<>();
    private final IntArguments intArguments;
    private volatile boolean isFinished = false;

    private class RowBuilder implements ExternalStepBuilder {
        int doubleIndex = 0;
        int intIndex = 0;
        final int rowIndex;

        private RowBuilder() {
            this.rowIndex = argumentsRowsIndex.getAndIncrement();
        }

        @Override
        public ExternalStepBuilder putArg(double argument) {
            doubleArguments.putDouble(rowIndex, doubleIndex++, argument);
            return this;
        }

        @Override
        public ExternalStepBuilder putArg(int argument) {
            doubleArguments.putDouble(rowIndex, intIndex++, argument);
            return this;
        }

        @Override
        public SubStep build(KernelCallBack callBack) {
            callBacks.add(rowIndex, callBack);
            return new SubStep() {
                @Override
                public void execute() {
                    // do nothing -> callback already been called
                }

                @Override
                public boolean canExecute() {
                    return isFinished;
                }
            };
        }

    }

    public GpuExecution(List<KernelArgument> kernelArguments) {
        this.doubleArguments = new DoubleArguments(kernelArguments);
        this.intArguments = new IntArguments(kernelArguments);
    }

    public void flush(Kernel kernel) {
        int gpuArgumentsNumber = argumentsRowsIndex.get();
        if (gpuArgumentsNumber == 0)
            return;
        try (KernelExecution kernelExecution = kernel.newExecution(gpuArgumentsNumber)) {
            Map<KernelArgument, double[]> doubleArrays = doubleArguments.getArrays(gpuArgumentsNumber);
            Map<KernelArgument, int[]> intArrays = intArguments.getArrays(gpuArgumentsNumber);
            double[][] doublesResults = doubleArrays.entrySet().stream()
                    .filter(entry -> entry.getKey().isOut())
                    .sorted(Comparator.comparingInt(entry -> entry.getKey().getArgumentIndex()))
                    .map(Map.Entry::getValue)
                    .toArray(double[][]::new);
            int[][] intResults = intArrays.entrySet().stream()
                    .filter(entry -> entry.getKey().isOut())
                    .sorted(Comparator.comparingInt(entry -> entry.getKey().getArgumentIndex()))
                    .map(Map.Entry::getValue)
                    .toArray(int[][]::new);

            doubleArrays.forEach(kernelExecution::bindParameter);

            kernelExecution.execute();
            for (int i = 0; i < callBacks.size(); i++) {
                final int row = i;
                callBacks.get(i).execute(new GpuReader() {
                    int doubleIndex = 0;
                    int intIndex = 0;

                    @Override
                    public double readDouble() {
                        return doublesResults[doubleIndex++][row];
                    }

                    @Override
                    public double readInt() {
                        return intResults[intIndex++][row];
                    }
                });
            }
            isFinished = true;
        }
    }

    public ExternalStepBuilder getStepBuilder() {
        return new RowBuilder();
    }
}