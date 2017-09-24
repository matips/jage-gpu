package org.jage.gpu.binding.jocl;

import org.apache.bcel.util.ClassLoader;
import org.jage.gpu.binding.*;
import org.jage.gpu.binding.jocl.arguments.DefaultJoclArgumentFactory;
import org.jage.gpu.binding.jocl.arguments.primitives.Int;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

public class AddressSpaceConfigurationGPUTest {

    private File kernelSource = new File(ClassLoader.getSystemResource("multipleWithGlobalTest.cl").getFile());
    private File resuctionSource = new File(ClassLoader.getSystemResource("reduction.cl").getFile());

    @Test
    public void testBuildKernel() throws Exception {
        AddressSpaceAutoConfiguration instance = new AddressSpaceAutoConfiguration();

        Kernel sampleKernel = instance.buildKernel(kernelSource, "multipleTest");

        assertEquals("multipleTest", sampleKernel.getKernelName());
        List<KernelArgument> arguments = sampleKernel.getArguments();
        assertEquals("height", arguments.get(0).getArgumentName());
        assertEquals("a1", arguments.get(1).getArgumentName());
        assertEquals("a2", arguments.get(2).getArgumentName());
        assertEquals("result", arguments.get(3).getArgumentName());

        for (int i = 0; i < 4; i++) {
            assertEquals(ArgumentAccessQualifier.NONE, arguments.get(i).getAccessQualifier());
        }
        assertEquals(DefaultJoclArgumentFactory.INSTANCE.fromClass(Int.class), arguments.get(0).getType());

        assertThat(arguments.get(0).getAddressQualifier()).isEqualTo(ArgumentAddressQualifier.PRIVATE);
        assertThat(arguments.get(1).getAddressQualifier()).isEqualTo(ArgumentAddressQualifier.PRIVATE);
        assertThat(arguments.get(2).getAddressQualifier()).isEqualTo(ArgumentAddressQualifier.CONSTANT);
        assertThat(arguments.get(3).getAddressQualifier()).isEqualTo(ArgumentAddressQualifier.GLOBAL);

        assertThat(arguments.get(0).isIn()).isTrue();
        assertThat(arguments.get(1).isIn()).isTrue();
        assertThat(arguments.get(2).isIn()).isTrue();
        assertThat(arguments.get(3).isIn()).isFalse();
        assertThat(arguments.get(3).isOut()).isTrue();
    }

    @Test
    public void testLocalAddressSpace() throws IOException {
        AddressSpaceAutoConfiguration instance = new AddressSpaceAutoConfiguration();
        instance.initialize();

        Kernel sampleKernel = instance.buildKernel(resuctionSource, "adding");
        Random random = new Random();
        int size = 500;
        KernelExecution kernelExecution = sampleKernel.newExecution(size);
        int[] in = new int[size];
        for (int i = 0; i < size; i++) {
            in[i] = random.nextInt(20);
        }
        int[] out = new int[2];
        int sum = Arrays.stream(in).sum();
//
        kernelExecution.bindParameter(sampleKernel.getArguments().get(0), size);
        kernelExecution.bindParameter(sampleKernel.getArguments().get(1), 512);
        kernelExecution.bindParameter(sampleKernel.getArguments().get(2), in);
        kernelExecution.bindParameter(sampleKernel.getArguments().get(3), out);

        kernelExecution.execute();
        assertThat(out[0]).isEqualTo(sum);
        instance.shutdown();
    }
}