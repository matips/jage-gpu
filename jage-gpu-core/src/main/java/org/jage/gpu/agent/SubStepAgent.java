package org.jage.gpu.agent;

import java.util.LinkedList;
import java.util.Queue;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.gpu.executors.ExternalExecutor;
import org.jage.gpu.executors.ExternalExecutorRegistry;

public abstract class SubStepAgent extends org.jage.agent.SimpleAgent {
    Queue<SubStep> subSteps = new LinkedList<>();
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
            subSteps.add(step);
        }
    }

    /**
     * Resume execution of agent (execute next subStep). If next step is not executable (ex. waiting for gpu exection) method do nothing
     *
     * @return true if all substeps are finished, false otherwise
     */
    public boolean resume() {
        SubStep head = subSteps.peek();
        while (head != null && head.canExecute()) {
            subSteps.poll();
            head.execute();
            head = subSteps.peek();
        }
        return subSteps.isEmpty();
    }

    protected ExternalExecutor getGpuStep(String kernelName) {
        return externalExecutorRegistry.get(kernelName);
    }

}
