package org.jage.gpu.workplace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.agent.ISimpleAgent;
import org.jage.gpu.agent.SubStepAgent;
import org.jage.gpu.executors.ExternalExecutorRegistry;

public abstract class SubStepAgentsWorkplace extends SimpleWorkplace {
    private List<Consumer<SubStepAgentsWorkplace>> steps = new ArrayList<>();
    protected ExternalExecutorRegistry externalExecutorRegistry;

    public SubStepAgentsWorkplace(AgentAddress address) {
        super(address);
    }

    public SubStepAgentsWorkplace(AgentAddressSupplier supplier) {
        super(supplier);
    }

    @Override
    protected void executeStepsOnAgents() {

        SubStepAgentsWorkplace.this.agents.values().forEach(ISimpleAgent::step);
        List<SubStepAgent> subStepAgents = SubStepAgentsWorkplace.this.agents.values().stream()
                .filter(agent -> agent instanceof SubStepAgent)
                .map(agent -> (SubStepAgent) agent)
                .collect(Collectors.toList());

        while (!subStepAgents.isEmpty()) {
            externalExecutorRegistry.flush();
            Iterator<SubStepAgent> iterator = subStepAgents.iterator();
            while (iterator.hasNext()) {
                SubStepAgent agent = iterator.next();
                agent.resume();
                iterator.remove();
            }
        }
    }

    public void setExternalExecutorRegistry(ExternalExecutorRegistry externalExecutorRegistry) {
        this.externalExecutorRegistry = (externalExecutorRegistry);
    }
}
