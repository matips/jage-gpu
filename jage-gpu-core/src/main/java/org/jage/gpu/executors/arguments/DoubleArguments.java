package org.jage.gpu.executors.arguments;

import static org.jage.gpu.binding.ArgumentType.DOUBLE_ARRAY;

import java.util.List;

import org.jage.gpu.binding.KernelArgument;

import com.carrotsearch.hppc.DoubleArrayList;

public class DoubleArguments extends PrimitiveArguments<DoubleArrayList, double[]> {
    private DoubleArrayList[] in;

    public DoubleArguments(List<KernelArgument> arguments) {
        super(arguments, DOUBLE_ARRAY, DoubleArrayList[]::new, DoubleArrayList::new, DoubleArrayList::toArray, double[]::new);
    }

    public void putDouble(int rowIndex, int argumentIndex, double value) {
        in[argumentIndex].insert(rowIndex, value);
    }

    @Override
    protected void setIn(DoubleArrayList[] inArray) {
        in = inArray;
    }

}
