package org.jage.gpu.random;

import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelExecution;
import org.jage.gpu.binding.jocl.AddressSpaceAutoConfiguration;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressSpaceConfigurationWithRandomGPUTest {

    private File kernelSource = new File(ClassLoader.getSystemResource("multipleWithGlobalTest.cl").getFile());

    @Test
    public void testBuildKernel() throws Exception {
        AddressSpaceAutoConfiguration instance = new AddressSpaceAutoConfiguration();
        instance.initialize();

        Kernel sampleKernel = instance.buildKernel(kernelSource, "multipleByRandom");

        KernelExecution kernelExecution = sampleKernel.newExecution(32);
        double[] in = new double[32];
        for (int i = 0; i < 32; i++) {
            in[i] = 456;
        }
        double[] out = new double[32];

        kernelExecution.bindParameter(sampleKernel.getArguments().get(0), 32);
        kernelExecution.bindParameter(sampleKernel.getArguments().get(1), new Random());
        kernelExecution.bindParameter(sampleKernel.getArguments().get(2), in);
        kernelExecution.bindParameter(sampleKernel.getArguments().get(3), out);

        kernelExecution.execute();
        long distinct = Arrays.stream(out).distinct().count();
        assertThat(distinct).isEqualTo(32l);

    }
}