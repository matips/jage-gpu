package pl.edu.agh.jage.gpu.examples.adding.agents;

import java.util.List;
import java.util.Random;

import org.jage.address.agent.AgentAddressSupplier;
import org.jage.gpu.agent.GpuAgent;
import org.jage.gpu.agent.SubStep;
import org.jage.gpu.executors.ExternalExecutor;

import pl.edu.agh.jage.gpu.examples.IGridFragment;

public class GPUGridFragment extends GpuAgent implements IGridFragment {

    private List<Integer> position;
    Random random = new Random();
    int step = 0;
    double summary = 0;
    private ExternalExecutor addingPos;

    public GPUGridFragment(AgentAddressSupplier supplier) {
        super(supplier);
    }

    @Override
    public void initialize(List<Integer> position) {
        addingPos = getGpuStep("addingPos");
        this.position = position;
    }

    @Override
    public void step() {
        SubStep subStep = addingPos
                .createStep()
                .putArg(summary)
                .putArg(position.get(0))
                .putArg(position.get(1))
                .putArg(step)
                .build(gpuReader -> summary = gpuReader.readDouble());

        postponeStep(subStep);
        step++;
    }

    @Override
    public List<Integer> getPosition() {
        return position;
    }
}
