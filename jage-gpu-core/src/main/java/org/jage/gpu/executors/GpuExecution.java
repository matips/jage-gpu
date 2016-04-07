package org.jage.gpu.executors;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.jage.gpu.ExternalStepBuilder;
import org.jage.gpu.KernelCallBack;
import org.jage.gpu.agent.GpuReader;
import org.jage.gpu.agent.SubStep;
import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.KernelExecution;
import org.jage.gpu.executors.arguments.DoubleArguments;
import org.jage.gpu.executors.arguments.IntArguments;

class GpuExecution {
    private final DoubleArguments doubleArguments;
    private final AtomicInteger argumentsRowsIndex = new AtomicInteger();
    private final IntArguments intArguments;
    private volatile boolean isFinished = false;
    private double[][] doublesResults;
    private int[][] intResults;

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
            return new SubStep() {
                @Override
                public void execute() {
                    callBack.execute(new GpuReader() {
                        int doubleIndex = 0;
                        int intIndex = 0;

                        @Override
                        public double readDouble() {
                            return doublesResults[doubleIndex++][rowIndex];
                        }

                        @Override
                        public double readInt() {
                            return intResults[intIndex++][rowIndex];
                        }
                    });
                }

                @Override
                public boolean canExecute() {
                    return isFinished;
                }
            };
        }

    }

    public GpuExecution(List<KernelArgument> kernelArguments) {
        this.doubleArguments = new DoubleArguments(filterArguments(kernelArguments, ArgumentType.DOUBLE_ARRAY));
        this.intArguments = new IntArguments(filterArguments(kernelArguments, ArgumentType.INT_ARRAY));
    }

    private List<KernelArgument> filterArguments(List<KernelArgument> kernelArguments, ArgumentType doubleArray) {
        return kernelArguments.stream().filter(kernelArgument -> kernelArgument.getType().equals(doubleArray)).collect(
                Collectors.toList());
    }

    public void flush(Kernel kernel) {
        int gpuArgumentsNumber = argumentsRowsIndex.get();
        if (gpuArgumentsNumber == 0)
            return;
        try (KernelExecution kernelExecution = kernel.newExecution(gpuArgumentsNumber)) {
            Map<KernelArgument, double[]> doubleArrays = doubleArguments.getArrays(gpuArgumentsNumber);
            Map<KernelArgument, int[]> intArrays = intArguments.getArrays(gpuArgumentsNumber);
            doublesResults = doubleArrays.entrySet().stream()
                    .filter(entry -> entry.getKey().isOut())
                    .sorted(Comparator.comparingInt(entry -> entry.getKey().getArgumentIndex()))
                    .map(Map.Entry::getValue)
                    .toArray(double[][]::new);
            intResults = intArrays.entrySet().stream()
                    .filter(entry -> entry.getKey().isOut())
                    .sorted(Comparator.comparingInt(entry -> entry.getKey().getArgumentIndex()))
                    .map(Map.Entry::getValue)
                    .toArray(int[][]::new);

            doubleArrays.forEach(kernelExecution::bindParameter);
            intArrays.forEach(kernelExecution::bindParameter);

            kernelExecution.execute();
            isFinished = true;
        }
    }

    public ExternalStepBuilder getStepBuilder() {
        return new RowBuilder();
    }
}