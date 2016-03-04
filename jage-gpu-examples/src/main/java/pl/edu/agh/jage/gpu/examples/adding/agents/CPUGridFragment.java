package pl.edu.agh.jage.gpu.examples.adding.agents;

import java.util.List;
import java.util.Random;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.agent.SimpleAgent;

import pl.edu.agh.jage.gpu.examples.IGridFragment;

public class CPUGridFragment extends SimpleAgent implements IGridFragment {

	private List<Integer> position;
	Random random = new Random();
	int step = 0;
	double summary = 0;

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
		double nextAdd =  (position.get(0) + position.get(1))* step;
		summary += nextAdd;
		step++;
	}

	@Override
	public List<Integer> getPosition() {
		return position;
	}
}
