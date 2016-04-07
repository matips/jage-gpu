package org.jage.gpu.workplace;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.agent.ISimpleAgent;
import org.jage.gpu.agent.SubStepAgent;
import org.jage.gpu.executors.ExternalExecutorRegistry;
import org.jage.property.PropertyField;
import org.jage.workplace.SimpleWorkplace;
import org.slf4j.LoggerFactory;

public abstract class SubStepAgentsWorkplace extends SimpleWorkplace {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SubStepAgentsWorkplace.class);
    protected ExternalExecutorRegistry externalExecutorRegistry;

    public SubStepAgentsWorkplace(AgentAddress address) {
        super(address);
    }

    public SubStepAgentsWorkplace(AgentAddressSupplier supplier) {
        super(supplier);
    }

    protected void executeStepsOnAgents() {

        SubStepAgentsWorkplace.this.agents.values().forEach(ISimpleAgent::step);
        List<SubStepAgent> remainSubStepAgents = SubStepAgentsWorkplace.this.agents.values().stream()
                .filter(agent -> agent instanceof SubStepAgent)
                .map(agent -> (SubStepAgent) agent)
                .collect(Collectors.toList());

        while (!remainSubStepAgents.isEmpty()) {
            externalExecutorRegistry.flush();
            Iterator<SubStepAgent> iterator = remainSubStepAgents.iterator();
            while (iterator.hasNext()) {
                SubStepAgent agent = iterator.next();
                if (agent.resume()) {
                    iterator.remove();
                }
            }
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
}
