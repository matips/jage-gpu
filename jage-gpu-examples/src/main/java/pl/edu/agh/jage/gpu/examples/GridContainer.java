package pl.edu.agh.jage.gpu.examples;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.platform.component.exception.ComponentException;
import org.jage.workplace.ConnectedSimpleWorkplace;

import pl.edu.agh.jage.gpu.examples.config.Configuration;

public class GridContainer extends ConnectedSimpleWorkplace {

    @Inject
    Configuration configuration;
    private long startTime;

    public GridContainer(AgentAddress address) {
        super(address);
    }

    public GridContainer(AgentAddressSupplier supplier) {
        super(supplier);
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
                IGridFragment gridFragment = instanceProvider.getInstance(IGridFragment.class);
                List<Integer> position = Arrays.asList(x, y);
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