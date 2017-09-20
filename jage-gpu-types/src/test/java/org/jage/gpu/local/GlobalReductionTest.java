package org.jage.gpu.local;

import com.google.common.collect.Sets;
import org.apache.bcel.util.ClassLoader;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelExecution;
import org.jage.gpu.binding.jocl.kernelAsFunction.KernelAsFunctionJoclGpu;
import org.jage.gpu.binding.jocl.kernelAsFunction.SimpleGPU;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalReductionTest {

    private File kernelSource = new File(ClassLoader.getSystemResource("reduction.cl").getFile());

    @Test
    public void testBuildKernel() throws Exception {
        SimpleGPU joclGpu = new SimpleGPU();
        Kernel kernel = joclGpu.buildKernel(kernelSource, "sum");


        KernelExecution kernelExecution = kernel.newExecution(20);

        int[] outArray = new int[20];
        kernelExecution.bindParameter(kernel.getArguments().get(1), 20 );
        kernelExecution.bindParameter(kernel.getArguments().get(1), outArray);
        kernelExecution.execute();

    }

}