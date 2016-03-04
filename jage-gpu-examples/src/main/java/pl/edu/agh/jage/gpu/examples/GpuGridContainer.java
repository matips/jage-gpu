package pl.edu.agh.jage.gpu.examples;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.gpu.executors.ExternalExecutorRegistry;
import org.jage.gpu.workplace.SubStepAgentsWorkplace;
import org.jage.platform.component.exception.ComponentException;

import pl.edu.agh.jage.gpu.examples.integrals.agents.GPUGridFragment;
import pl.edu.agh.jage.gpu.examples.config.Configuration;

public class GpuGridContainer extends SubStepAgentsWorkplace {

    private long startTime;
    @Inject
    Configuration configuration;

    public GpuGridContainer(AgentAddress address) {
        super(address);
    }

    public GpuGridContainer(AgentAddressSupplier supplier) {
        super(supplier);
    }
    public GpuGridContainer(AgentAddressSupplier supplier, ExternalExecutorRegistry externalExecutorRegistry) {
        super(supplier);
        setExternalExecutorRegistry(externalExecutorRegistry);
    }

    @Override
    public void start() {
        super.start();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void init() throws ComponentException {
        super.init();

        for (int x = 0; x < configuration.getxSize(); x++) {
            for (int y = 0; y < configuration.getySize(); y++) {
                GPUGridFragment gridFragment = instanceProvider.getInstance(GPUGridFragment.class);
                List<Integer> position = Arrays.asList(x, y);
                gridFragment.initialize(externalExecutorRegistry);
                gridFragment.initialize(position);
                add(gridFragment);
            }
        }
    }

    @Override
    public boolean finish() throws ComponentException {
        System.out.println(String.format("Simulation time: %f s", (System.currentTimeMillis() - startTime) / 1000.0));
        return super.finish();
    }

}