package pl.edu.agh.jage.gpu.examples.integrals.agents;

import java.util.List;
import java.util.Random;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.agent.SimpleAgent;

import pl.edu.agh.jage.gpu.examples.IGridFragment;

public class CPUGridFragment extends SimpleAgent implements IGridFragment {

    public static final double INTEGRAL_STEP = 0.0001;
    private List<Integer> position;
    Random random = new Random();
    double energy = 0;

    public CPUGridFragment(AgentAddress address) {
        super(address);
    }

    public CPUGridFragment(AgentAddressSupplier supplier) {
        super(supplier);
    }

    @Override
    public void initialize(List<Integer> position) {
        this.position = position;
    }

    @Override
    public void step() {
        double left = (-random.nextDouble()) * Math.PI;
        double right = random.nextDouble() * Math.PI;
        energy += inegral(left, right);

    }

    private double inegral(double left_bound, double right_bound) {
        double result = 0;
        double h = INTEGRAL_STEP;
        for (double y = left_bound; y < right_bound; y += h) {
            result += Math.sin(y);
        }
        return result * h;
    }

    @Override
    public List<Integer> getPosition() {
        return position;
    }
}
