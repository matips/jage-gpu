package pl.edu.agh.jage.gpu.examples.random;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.gpu.executors.ExternalExecutor;
import org.jage.gpu.executors.ExternalExecutorRegistry;
import pl.edu.agh.jage.gpu.examples.GpuGridContainer;
import pl.edu.agh.jage.gpu.examples.random.agents.GPUGridFragment;

import java.util.Random;

public class GpuRandomGridContainer extends GpuGridContainer {
    {
        type = GPUGridFragment.class;
    }

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
    public void step() {
        ExternalExecutor randomTestExecutor = externalExecutorRegistry.get("random_test");
        randomTestExecutor.bindGlobalArgument(random);
        super.step();
    }
}