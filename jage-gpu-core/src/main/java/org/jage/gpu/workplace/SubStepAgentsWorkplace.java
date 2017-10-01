package org.jage.gpu.workplace;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.agent.ISimpleAgent;
import org.jage.gpu.Resumable;
import org.jage.gpu.executors.ExternalExecutorRegistry;
import org.jage.property.PropertyField;
import org.jage.workplace.SimpleWorkplace;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SubStepAgentsWorkplace extends SimpleWorkplace {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SubStepAgentsWorkplace.class);
    protected ExternalExecutorRegistry externalExecutorRegistry;

    public SubStepAgentsWorkplace(AgentAddress address) {
        super(address);
    }

    public SubStepAgentsWorkplace(AgentAddressSupplier supplier) {
        super(supplier);
    }

    protected void executeStepsOnAgents() {

        agents.values().forEach(ISimpleAgent::step);

        executeSubSteps();
    }

    void executeSubSteps() {
        List<Resumable> remainSubStepAgents = agents.values().stream()
                .filter(agent -> agent instanceof Resumable)
                .map(agent -> (Resumable) agent)
                .collect(Collectors.toList());

        while (!remainSubStepAgents.isEmpty()) {
            externalExecutorRegistry.flush();
            remainSubStepAgents.removeIf(Resumable::resume);
        }
    }

    @Override
    public void step() {
        this.withReadLock(this::executeStepsOnAgents);
        this.getActionService().processActions();
        incrementStep();
        this.notifyMonitorsForChangedProperties();
    }

    static private final Field stepField;

    static {
        stepField = Arrays.stream(SimpleWorkplace.class.getDeclaredFields())
                .filter(filed -> filed.getAnnotation(PropertyField.class) != null && filed.getAnnotation(PropertyField.class).propertyName().equals("step"))
                .findAny()
                .get();
        stepField.setAccessible(true);
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

    @Override
    public long getStep() {
        return super.getStep();
    }

    public void setExternalExecutorRegistry(ExternalExecutorRegistry externalExecutorRegistry) {
        this.externalExecutorRegistry = externalExecutorRegistry;
    }

    public ExternalExecutorRegistry getExternalExecutorRegistry() {
        return externalExecutorRegistry;
    }
}
