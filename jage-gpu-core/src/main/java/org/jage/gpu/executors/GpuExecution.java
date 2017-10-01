package org.jage.gpu.executors;

import org.jage.gpu.ExternalStepBuilder;
import org.jage.gpu.KernelCallBack;
import org.jage.gpu.agent.GpuReader;
import org.jage.gpu.agent.SubStep;
import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.KernelExecution;
import org.jage.gpu.binding.jocl.argumentAutoConfig.InOutDouble;
import org.jage.gpu.binding.jocl.arguments.arrays.DoubleArray;
import org.jage.gpu.binding.jocl.arguments.arrays.IntArray;
import org.jage.gpu.executors.arguments.AgentsCount;
import org.jage.gpu.executors.arguments.DoubleArguments;
import org.jage.gpu.executors.arguments.IntArguments;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

class GpuExecution {
    private final DoubleArguments doubleArguments;
    private final IntArguments intArguments;

    private final AtomicInteger[] argumentsRowsIndexes;
    private final AgentLevel[] agetnsLevels;

    private volatile boolean isFinished = false;
    private double[][] doublesResults;
    private int[][] intResults;
    private KernelArgument[] globalArguments;
    private LinkedList<Object> bindedGlobalArguments = new LinkedList<>();

    private class AgentLevel {
        KernelArgument agentCountArgument;
        int intShift, doubleShift, intResultShift, doubleResultShift;

        AgentLevel(AgentLevel previous) {
            intShift = previous.intShift;
            doubleShift = previous.doubleShift;
            intResultShift = previous.intResultShift;
            doubleResultShift = previous.doubleResultShift;
        }

        AgentLevel() {

        }
    }

    public void bindGlobalArgument(Object argument) {
        bindedGlobalArguments.add(argument);
    }

    private class RowBuilder implements ExternalStepBuilder {
        private final int agentLevel;
        int doubleIndex = 0;
        int intIndex = 0;
        final int rowIndex;

        private RowBuilder(int agentLevel) {
            this.rowIndex = argumentsRowsIndexes[agentLevel].getAndIncrement();
            doubleIndex = agetnsLevels[agentLevel].doubleShift;
            intIndex = agetnsLevels[agentLevel].intShift;
            this.agentLevel = agentLevel;
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
        public ExternalStepBuilder putIndex(int level, int diff) {
            intArguments.putInt(rowIndex, intIndex++, argumentsRowsIndexes[level].get() + diff);
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
                    int doubleIndex = agetnsLevels[agentLevel].doubleResultShift;
                    int intIndex = agetnsLevels[agentLevel].intResultShift;

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
        this.doubleArguments = new DoubleArguments(filterArguments(kernelArguments, DoubleArray.class, InOutDouble.class));
        this.intArguments = new IntArguments(filterArguments(kernelArguments, IntArray.class));
        this.globalArguments = kernelArguments.stream()
                .filter(kernelArgument ->
                        (
                                !isSpecificParameter(kernelArgument, IntArray.class)
                                        && !isSpecificParameter(kernelArgument, DoubleArray.class)
                                        && !isSpecificParameter(kernelArgument, AgentsCount.class)
                                        && !isSpecificParameter(kernelArgument, InOutDouble.class)
                        )
                )
                .toArray(KernelArgument[]::new);

        AgentLevel agentLevel = new AgentLevel();
        agentLevel.agentCountArgument = kernelArguments.get(0);
        ArrayList<AgentLevel> levels = new ArrayList<>();
        int i = 1;
        do {
            levels.add(agentLevel);
            agentLevel = new AgentLevel(agentLevel);
            while (i < kernelArguments.size()) {
                if (isSpecificParameter(kernelArguments.get(i), AgentsCount.class)) {
                    agentLevel.agentCountArgument = kernelArguments.get(i);
                    i++;
                    break;
                } else if (isSpecificParameter(kernelArguments.get(i), DoubleArray.class)) {
                    if (kernelArguments.get(i).isIn()) {
                        agentLevel.doubleShift++;
                    }
                    if (kernelArguments.get(i).isOut()) {
                        agentLevel.doubleResultShift++;
                    }
                } else if (isSpecificParameter(kernelArguments.get(i), IntArray.class) && kernelArguments.get(i).isIn()) {
                    if (kernelArguments.get(i).isIn()) {
                        agentLevel.intShift++;
                    }
                    if (kernelArguments.get(i).isOut()) {
                        agentLevel.intResultShift++;
                    }
                }
                i++;
            }
        } while (i < kernelArguments.size());

        this.agetnsLevels = levels.toArray(new AgentLevel[levels.size()]);

        argumentsRowsIndexes = levels.stream()
                .map(ignore -> new AtomicInteger())
                .toArray(AtomicInteger[]::new);
    }

    private List<KernelArgument> filterArguments(List<KernelArgument> kernelArguments, Class<? extends ArgumentType>... argumentTypes) {
        return kernelArguments.stream()
                .filter(kernelArgument -> Arrays.stream(argumentTypes)
                        .anyMatch(argumentType -> isSpecificParameter(kernelArgument, argumentType))
                )
                .collect(Collectors.toList());
    }

    private boolean isSpecificParameter(KernelArgument kernelArgument, Class<? extends ArgumentType> argumentType) {
        return kernelArgument.getType().getClass().isAssignableFrom(argumentType);
    }

    public void flush(Kernel kernel) {
        int gpuArgumentsNumber = Arrays.stream(argumentsRowsIndexes)
                .mapToInt(AtomicInteger::get)
                .max().getAsInt();

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

            for (int i = 0; i < argumentsRowsIndexes.length; i++) {
                int agentsCount = argumentsRowsIndexes[i].get();
                kernelExecution.bindParameter(agetnsLevels[i].agentCountArgument, agentsCount);
            }
            kernelExecution.execute();
            isFinished = true;
        }
    }

    public ExternalStepBuilder getStepBuilder(int level) {
        return new RowBuilder(level);
    }
}