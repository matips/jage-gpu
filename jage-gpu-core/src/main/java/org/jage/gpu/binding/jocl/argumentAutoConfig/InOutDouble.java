package org.jage.gpu.binding.jocl.argumentAutoConfig;

import org.jage.gpu.binding.jocl.arguments.JoclKernelArgument;
import org.jage.gpu.binding.jocl.arguments.arrays.DoubleArray;

import java.util.Arrays;
import java.util.List;

@JoclKernelArgument
@InOut
public class InOutDouble extends DoubleArray {
    @Override
    public List<String> getCNames() {
        return Arrays.asList("InOutDouble");
    }
}