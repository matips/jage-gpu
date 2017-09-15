package org.jage.gpu.workplace;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.agent.ISimpleAgent;
import org.jage.agent.SimpleAggregate;
import org.jage.gpu.agent.SubStepAgent;
import org.jage.gpu.executors.ExternalExecutorRegistry;
import org.jage.property.PropertyField;
import org.jage.workplace.SimpleWorkplace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubStepSimpleAggregate extends SimpleAggregate {
    private static final Logger LOGGER = LoggerFactory.getLogger(SubStepSimpleAggregate.class);
    protected ExternalExecutorRegistry externalExecutorRegistry;

    public SubStepSimpleAggregate(AgentAddress address) {
        super(address);
    }

    public SubStepSimpleAggregate(AgentAddressSupplier supplier) {
        super(supplier);
    }

    protected void executeStepsOnAgents() {

        agents.values().forEach(ISimpleAgent::step);
        List<SubStepAgent> remainSubStepAgents = agents.values().stream()
                .filter(agent -> agent instanceof SubStepAgent)
                .map(agent -> (SubStepAgent) agent)
                .collect(Collectors.toList());

        while (!remainSubStepAgents.isEmpty()) {
            preExternalFlush();
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

    protected void preExternalFlush() {
        //bind some global variables;
    }

    @Override
    public void step() {
        this.withReadLock(this::executeStepsOnAgents);
        this.getActionService().processActions();
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

    public void setExternalExecutorRegistry(ExternalExecutorRegistry externalExecutorRegistry) {
        this.externalExecutorRegistry = externalExecutorRegistry;
    }

    public ExternalExecutorRegistry getExternalExecutorRegistry() {
        return externalExecutorRegistry;
    }
}
