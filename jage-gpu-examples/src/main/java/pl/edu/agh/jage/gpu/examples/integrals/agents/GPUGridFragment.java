package pl.edu.agh.jage.gpu.examples.integrals.agents;

import java.util.List;
import java.util.Random;

import org.jage.address.agent.AgentAddressSupplier;
import org.jage.gpu.agent.SubStep;
import org.jage.gpu.agent.SubStepAgent;
import org.jage.gpu.executors.ExternalExecutor;

import pl.edu.agh.jage.gpu.examples.IGridFragment;

public class GPUGridFragment extends SubStepAgent implements IGridFragment {

    private List<Integer> position;
    Random random = new Random();
    double energy = 0;
    private ExternalExecutor integralOnGpu;

    public GPUGridFragment(AgentAddressSupplier supplier) {
        super(supplier);
    }

    @Override
    public void initialize(List<Integer> position) {
        integralOnGpu = getGpuStep("integral");
        this.position = position;
    }

    @Override
    public void step() {
        double left = (-random.nextDouble()) * Math.PI;
        double right = random.nextDouble() * Math.PI;
        SubStep subStep = integralOnGpu
                .createStep()
                .putArg(left)
                .putArg(right)
                .build(gpuReader -> energy += gpuReader.readDouble());

        postponeStep(subStep);
    }

    @Override
    public List<Integer> getPosition() {
        return position;
    }
}
