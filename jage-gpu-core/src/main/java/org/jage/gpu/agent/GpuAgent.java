package org.jage.gpu.agent;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.gpu.executors.ExternalExecutor;
import org.jage.gpu.executors.ExternalExecutorRegistry;

public abstract class GpuAgent extends SubStepAgent implements IGpuAgent {
    private ExternalExecutorRegistry externalExecutorRegistry;

    public GpuAgent(AgentAddress address) {
        super(address);
    }

    public GpuAgent(AgentAddressSupplier supplier) {
        super(supplier);
    }

    public void initialize(ExternalExecutorRegistry externalExecutorRegistry) {
        this.externalExecutorRegistry = externalExecutorRegistry;
    }

    public ExternalExecutor getGpuStep(String kernelName) {
        return externalExecutorRegistry.get(kernelName);
    }

}
