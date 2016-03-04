package org.jage.gpu.agent;

import java.util.LinkedList;
import java.util.Queue;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.gpu.executors.ExternalExecutor;
import org.jage.gpu.executors.ExternalExecutorRegistry;

public abstract class SubStepAgent extends org.jage.agent.SimpleAgent {
    Queue<SubStep> gpuAgentSubSteps = new LinkedList<>();
    protected ExternalExecutorRegistry externalExecutorRegistry;

    public SubStepAgent(AgentAddress address) {
        super(address);
    }

    public SubStepAgent(AgentAddressSupplier supplier) {
        super(supplier);
    }

    public void initialize(ExternalExecutorRegistry externalExecutorRegistry) {
        this.externalExecutorRegistry = externalExecutorRegistry;
    }

    protected void postponeStep(SubStep step) {
        if (step.canExecute()) {
            step.execute();
        } else {
            gpuAgentSubSteps.add(step);
        }
    }

    /**
     * Resume execution of agent (execute next subStep). If next step is not executable (ex. waiting for gpu exection) method do nothing
     *
     * @return true if all substeps are finished, false otherwise
     */
    public boolean resume() {
        SubStep head = gpuAgentSubSteps.peek();
        if (head != null && head.canExecute()) {
            gpuAgentSubSteps.poll();
            head.execute();
        }
        return gpuAgentSubSteps.isEmpty();
    }

    protected ExternalExecutor getGpuStep(String kernelName) {
        return externalExecutorRegistry.get(kernelName);
    }

}
