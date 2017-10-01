package org.jage.gpu.binding.jocl.argumentAutoConfig;

import org.jage.gpu.binding.jocl.arguments.DefaultJoclArgumentFactory;
import org.jage.gpu.binding.jocl.arguments.arrays.DoubleArray;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.GlobalArgument;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.PrimitiveWrapper;

import java.util.Arrays;
import java.util.List;

/**
 * CRIF IT Solutions Poland
 */
@GlobalArgument
public class InOutDouble extends PrimitiveWrapper<double[]> {
    public InOutDouble() {
        super(DefaultJoclArgumentFactory.INSTANCE.fromClass(DoubleArray.class));
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("InOutDouble");
    }

    @Override
    public String getCName() {
        return "InOutDouble";
    }
}