package org.jage.gpu.executors;

import org.jage.gpu.ExternalStepBuilder;
import org.jage.gpu.KernelCallBack;
import org.jage.gpu.agent.GpuReader;
import org.jage.gpu.agent.SubStep;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.KernelExecution;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.GlobalArgument;
import org.jage.gpu.executors.arguments.DoubleArguments;
import org.jage.gpu.executors.arguments.IntArguments;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

class GpuExecution {
    private final DoubleArguments doubleArguments;
    private final IntArguments intArguments;

    private final AtomicInteger argumentsRowsIndex = new AtomicInteger();
    private volatile boolean isFinished = false;
    private double[][] doublesResults;
    private int[][] intResults;
    private KernelArgument[] globalArguments;
    private LinkedList<Object> bindedGlobalArguments = new LinkedList<>();

    public void bindGlobalArgument(Object argument) {
        bindedGlobalArguments.add(argument);
    }

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
            intArguments.putInt(rowIndex, intIndex++, argument);
            return this;
        }

        @Override
        public SubStep build(KernelCallBack callBack) {
            return new GpuSubStep(callBack);
        }

        private class GpuSubStep implements SubStep {
            private final KernelCallBack callBack;

            public GpuSubStep(KernelCallBack callBack) {
                this.callBack = callBack;
            }

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
                    public int readInt() {
                        return intResults[intIndex++][rowIndex];
                    }

                    @Override
                    public boolean readBoolean() {
                        return readInt() != 0; //there is no boolean type in c - there is int only
                    }
                });
            }

            @Override
            public boolean canExecute() {
                return isFinished;
            }
        }
    }

    public GpuExecution(List<KernelArgument> kernelArguments) {
        this.doubleArguments = new DoubleArguments(filterArguments(kernelArguments, double[].class));
        this.intArguments = new IntArguments(filterArguments(kernelArguments, int[].class));
        this.globalArguments = kernelArguments.stream()
                .filter(kernelArgument ->
                        isMarkAsGlobal(kernelArgument) || (
                                !kernelArgument.getType().is(int[].class)
                                        && !kernelArgument.getType().is(double[].class)
                        )
                )
                .skip(1) //skip first argument - by convection it is number of agents and it filled by KernelExecution
                .toArray(KernelArgument[]::new);
    }

    private List<KernelArgument> filterArguments(List<KernelArgument> kernelArguments, Class argumentType) {
        return kernelArguments.stream()
                .filter(kernelArgument -> kernelArgument.getType().is(argumentType))
                .filter(kernelArgument -> !isMarkAsGlobal(kernelArgument))
                .collect(Collectors.toList());
    }

    private boolean isMarkAsGlobal(KernelArgument kernelArgument) {
        return kernelArgument.getType().getClass().getAnnotation(GlobalArgument.class) != null;
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
            for (int i = 0; i < globalArguments.length; i++) {
                kernelExecution.bindParameter(globalArguments[i], bindedGlobalArguments.get(i));
            }

            kernelExecution.execute();
            isFinished = true;
        }
    }

    public ExternalStepBuilder getStepBuilder() {
        return new RowBuilder();
    }
}