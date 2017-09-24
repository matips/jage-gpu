package org.jage.gpu.random;

import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.AddressSpaceAutoConfiguration;
import org.jage.gpu.global.arrays.GlobalDoubleArray;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CRIF IT Solutions Poland
 */
public class GlobalDoubleArrayTest {
    private File kernelSource = new File(ClassLoader.getSystemResource("globalArray.cl").getFile());
    @Test
    public void name() throws Exception {
        AddressSpaceAutoConfiguration instance = new AddressSpaceAutoConfiguration();
        Kernel kernel = instance.buildKernel(kernelSource, "globalArray");

        List<KernelArgument> arguments = kernel.getArguments();

        assertThat(arguments.get(0).getType()).isOfAnyClassIn(GlobalDoubleArray.class);
    }
}
