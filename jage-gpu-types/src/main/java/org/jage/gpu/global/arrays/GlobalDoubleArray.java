package org.jage.gpu.global.arrays;

import org.jage.gpu.binding.jocl.arguments.JoclKernelArgument;
import org.jage.gpu.binding.jocl.arguments.arrays.DoubleArray;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.GlobalArgument;

import java.util.Arrays;
import java.util.List;

@GlobalArgument
@JoclKernelArgument
public class GlobalDoubleArray extends DoubleArray {
    @Override
    public List<String> getCNames() {
        return Arrays.asList("GlobalDoubleArray");
    }
}