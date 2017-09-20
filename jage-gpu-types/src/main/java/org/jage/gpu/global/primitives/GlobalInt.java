package org.jage.gpu.global.primitives;

import org.jage.gpu.binding.jocl.arguments.DefaultJoclArgumentFactory;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.GlobalArgument;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.PrimitiveWrapper;

import java.util.Arrays;
import java.util.List;

@GlobalArgument
class GlobalInt extends PrimitiveWrapper<Integer> {
    public GlobalInt() {
        super(DefaultJoclArgumentFactory.INSTANCE.fromClass(Integer.class));
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("GlobalInt");
    }
}