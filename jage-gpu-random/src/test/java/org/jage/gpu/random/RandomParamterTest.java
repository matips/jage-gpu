package org.jage.gpu.random;

import com.google.common.collect.Sets;
import org.apache.bcel.util.ClassLoader;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelExecution;
import org.jage.gpu.binding.jocl.kernelAsFunction.KernelAsFunctionJoclGpu;
import org.junit.Test;

import java.io.File;
import java.util.Random;

public class RandomParamterTest {

    private File kernelSource = new File(ClassLoader.getSystemResource("randomAC.cl").getFile());

    @Test
    public void testBuildKernel() throws Exception {
        KernelAsFunctionJoclGpu instance = new KernelAsFunctionJoclGpu();

        Kernel kernel = instance.buildKernel(kernelSource, "random_test", Sets.newHashSet("a1", "a2"), Sets.newHashSet("result"));

        KernelExecution kernelExecution = kernel.newExecution(20);
        Random random = new Random();
        double[] array = new double[20];

        kernelExecution.bindParameter(kernel.getArguments().get(1), random);
        kernelExecution.bindParameter(kernel.getArguments().get(2), array);
        kernelExecution.execute();
        for (int i = 0; i < 20; i++) {
            System.out.println(array[i]);
        }
    }

}