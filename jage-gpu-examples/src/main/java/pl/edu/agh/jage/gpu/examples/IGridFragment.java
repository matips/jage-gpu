package pl.edu.agh.jage.gpu.examples;

import java.util.List;

import org.jage.agent.ISimpleAgent;

public interface IGridFragment extends ISimpleAgent{
	void initialize(List<Integer> position);

	List<Integer> getPosition();
}
