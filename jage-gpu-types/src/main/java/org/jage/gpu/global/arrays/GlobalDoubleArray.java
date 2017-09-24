package org.jage.gpu.global.arrays;

import org.jage.gpu.binding.jocl.arguments.DefaultJoclArgumentFactory;
import org.jage.gpu.binding.jocl.arguments.arrays.DoubleArray;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.GlobalArgument;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.PrimitiveWrapper;

import java.util.Arrays;
import java.util.List;

@GlobalArgument
public class GlobalDoubleArray extends PrimitiveWrapper<double[]> {
    public GlobalDoubleArray() {
        super(DefaultJoclArgumentFactory.INSTANCE.fromClass(DoubleArray.class));
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("GlobalDoubleArray");
    }
}