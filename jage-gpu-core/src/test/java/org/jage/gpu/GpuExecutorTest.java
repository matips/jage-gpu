package org.jage.gpu;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Random;

import org.apache.bcel.util.ClassLoader;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelExecution;
import org.jage.gpu.binding.jocl.JoclGpu;
import org.jage.gpu.binding.jocl.kernelAsFunction.KernelAsFunctionJoclGpu;
import org.junit.Test;

import com.google.common.collect.Sets;

public class GpuExecutorTest {

    private File kernelSource = new File(ClassLoader.getSystemResource("simpleAddingTest.cl").getFile());
    private File argumentsTestKernelAsFunction = new File(ClassLoader.getSystemResource("argumentsTestKernelAsFunction.cl").getFile());
    private File simpleAddingInPlaceTestSource = new File(ClassLoader.getSystemResource("simpleAddingInPlaceTest.cl").getFile());

    @Test
    public void testExecute() throws Exception {
        JoclGpu joclGpu = new JoclGpu();
        joclGpu.initialize();
        Kernel kernel = joclGpu.buildKernel(kernelSource, "simpleAddingTest", Sets.newHashSet("a1", "a2"), Sets.newHashSet("result"));
        KernelExecution kernelExecution = kernel.newExecution(20);
        Random random = new Random();
        double[][] arrays = new double[3][];
        for (int i = 0; i < 3; i++) {
            arrays[i] = new double[20];
        }
        for (int i = 0; i < 20; i++) {
            arrays[0][i] = random.nextDouble();
            arrays[1][i] = random.nextDouble();
        }
        for (int i = 0; i < 3; i++) {
            kernelExecution.bindParameter(kernel.getArguments().get(i + 1), arrays[i]);
        }
        kernelExecution.execute();
        for (int i = 0; i < 20; i++) {
            assertEquals(arrays[0][i] + arrays[1][i], arrays[2][i], 0.00001);
        }
    }
    @Test
    public void testArgumentsTestKernelAsFunction() throws Exception {
        KernelAsFunctionJoclGpu joclGpu = new KernelAsFunctionJoclGpu();
        Kernel kernel = joclGpu.buildKernel(argumentsTestKernelAsFunction, "adding", Sets.newHashSet("a1", "a2"), Sets.newHashSet("result"));
        KernelExecution kernelExecution = kernel.newExecution(20);
        Random random = new Random();
        double[][] arrays = new double[3][];
        for (int i = 0; i < 3; i++) {
            arrays[i] = new double[20];
        }
        for (int i = 0; i < 20; i++) {
            arrays[0][i] = random.nextDouble();
            arrays[1][i] = random.nextDouble();
        }
        for (int i = 0; i < 3; i++) {
            kernelExecution.bindParameter(kernel.getArguments().get(i + 1), arrays[i]);
        }
        kernelExecution.execute();
        for (int i = 0; i < 20; i++) {
            assertEquals(arrays[0][i] + arrays[1][i], arrays[2][i], 0.00001);
        }
    }

    @Test
    public void testExecuteWithInOut() throws Exception {
        JoclGpu joclGpu = new JoclGpu();
        joclGpu.initialize();
        Kernel kernel = joclGpu
                .buildKernel(simpleAddingInPlaceTestSource, "simpleAddingTest", Sets.newHashSet("a1", "a2results"), Sets.newHashSet("a2results"));
        KernelExecution kernelExecution = kernel.newExecution(20);
        Random random = new Random();
        double[][] arrays = new double[3][];
        for (int i = 0; i < 3; i++) {
            arrays[i] = new double[20];
        }
        for (int i = 0; i < 20; i++) {
            arrays[0][i] = random.nextDouble();
            arrays[1][i] = random.nextDouble();
            arrays[2][i] = arrays[1][i] + arrays[0][i];
        }
        for (int i = 0; i < 2; i++) {
            kernelExecution.bindParameter(kernel.getArguments().get(i + 1), arrays[i]);
        }
        kernelExecution.execute();
        for (int i = 0; i < 20; i++) {
            assertEquals(arrays[2][i], arrays[1][i], 0.00001);
        }
    }

}