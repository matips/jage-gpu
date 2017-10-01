package org.jage.gpu.workplace;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.agent.ISimpleAgent;
import org.jage.agent.SimpleAggregate;
import org.jage.gpu.Resumable;
import org.jage.gpu.agent.SubStep;
import org.jage.gpu.executors.ExternalExecutor;
import org.jage.gpu.executors.ExternalExecutorRegistry;
import org.jage.property.PropertyField;
import org.jage.workplace.SimpleWorkplace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class SubStepSimpleAggregate extends SimpleAggregate implements Resumable {
    Queue<SubStep> subSteps = new LinkedList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(SubStepSimpleAggregate.class);
    protected ExternalExecutorRegistry externalExecutorRegistry;

    public void initialize(ExternalExecutorRegistry externalExecutorRegistry) {
        this.externalExecutorRegistry = externalExecutorRegistry;
    }

    public SubStepSimpleAggregate(AgentAddress address) {
        super(address);
    }

    public SubStepSimpleAggregate(AgentAddressSupplier supplier) {
        super(supplier);
    }

    public ExternalExecutor getGpuStep(String kernelName) {
        return externalExecutorRegistry.get(kernelName);
    }

    @Override
    public void postponeStep(SubStep step) {
        subSteps.add(step);
    }

    @Override
    public boolean resume() {
        SubStep head = subSteps.peek();
        while (head != null && head.canExecute()) {
            subSteps.poll();
            head.execute();
            head = subSteps.peek();
        }
        boolean finished = subSteps.isEmpty() && executeSubSteps();
        if (finished)
            endStep();

        return finished;
    }

    @Override
    public void step() {
        withReadLock(new Runnable() {
            @Override
            public void run() {
                for (final ISimpleAgent agent : agents.values()) {
                    agent.step();
                }
            }
        });

        endStep();
    }

    public void endStep() {
        getActionService().processActions();
        notifyMonitorsForChangedProperties();
    }

    boolean executeSubSteps() {
        List<Resumable> remainSubStepAgents = agents.values().stream()
                .filter(agent -> agent instanceof Resumable)
                .map(agent -> (Resumable) agent)
                .collect(Collectors.toList());

        remainSubStepAgents.removeIf(Resumable::resume);
        return remainSubStepAgents.isEmpty();
    }
    /**
     * This is workaround, because for unknown reason SimpleWorkplace#step is private and incremented only in SimpleWorkplace#step() witch I want to overwrite
     * todo: propose change in jAge core
     */
    public void incrementStep() {
        try {
            stepField.setLong(this, getStep() + 1);
        } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public long getStep(){
        try {
            return stepField.getLong(this);
        } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return 0;
    }

    static private final Field stepField;

    static {
        stepField = Arrays.stream(SimpleWorkplace.class.getDeclaredFields())
                .filter(filed -> filed.getAnnotation(PropertyField.class) != null && filed.getAnnotation(PropertyField.class).propertyName().equals("step"))
                .findAny()
                .get();
        stepField.setAccessible(true);
    }

}
