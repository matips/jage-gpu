package pl.edu.agh.jage.gpu.examples.random;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.gpu.executors.ExternalExecutor;
import org.jage.gpu.executors.ExternalExecutorRegistry;
import pl.edu.agh.jage.gpu.examples.GpuGridContainer;

import java.util.Random;

public class GpuRandomGridContainer extends GpuGridContainer {
    Random random = new Random();

    public GpuRandomGridContainer(AgentAddress address) {
        super(address);
    }

    public GpuRandomGridContainer(AgentAddressSupplier supplier) {
        super(supplier);
    }

    public GpuRandomGridContainer(AgentAddressSupplier supplier, ExternalExecutorRegistry externalExecutorRegistry) {
        super(supplier, externalExecutorRegistry);
    }

    @Override
    protected void preExternalFlush() {
        ExternalExecutor randomTestExecutor = externalExecutorRegistry.get("random_test");
        randomTestExecutor.bindGlobalArgument(random);
        super.preExternalFlush();
    }
}