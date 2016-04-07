package org.jage.gpu.agent;

import java.util.LinkedList;
import java.util.Queue;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;

public abstract class SubStepAgent extends org.jage.agent.SimpleAgent {
    Queue<SubStep> subSteps = new LinkedList<>();

    public SubStepAgent(AgentAddress address) {
        super(address);
    }

    public SubStepAgent(AgentAddressSupplier supplier) {
        super(supplier);
    }

    protected void postponeStep(SubStep step) {
        subSteps.add(step);
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

}
