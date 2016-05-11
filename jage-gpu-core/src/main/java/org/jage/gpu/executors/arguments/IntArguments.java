package org.jage.gpu.executors.arguments;

import java.util.List;

import org.jage.gpu.binding.KernelArgument;

import com.carrotsearch.hppc.IntArrayList;

public class IntArguments extends PrimitiveArguments<IntArrayList, int[]> {
    private IntArrayList[] in;

    public IntArguments(List<KernelArgument> arguments) {
        super(arguments, int[].class, IntArrayList[]::new, IntArrayList::new, IntArrayList::toArray, int[]::new);
    }

    public void putInt(int rowIndex, int argumentIndex, int value) {
        in[argumentIndex].insert(rowIndex, value);
    }

    @Override
    protected void setIn(IntArrayList[] inArray) {
        in = inArray;
    }

}
