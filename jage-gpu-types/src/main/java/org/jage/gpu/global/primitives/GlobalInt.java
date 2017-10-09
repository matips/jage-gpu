package org.jage.gpu.global.primitives;

import org.jage.gpu.binding.jocl.arguments.JoclKernelArgument;
import org.jage.gpu.binding.jocl.arguments.primitives.Int;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.GlobalArgument;

import java.util.Arrays;
import java.util.List;

@GlobalArgument
@JoclKernelArgument
class GlobalInt extends Int {
    @Override
    public List<String> getCNames() {
        return Arrays.asList("GlobalInt");
    }
}