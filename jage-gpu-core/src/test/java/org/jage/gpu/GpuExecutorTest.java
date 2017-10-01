package org.jage.gpu;

import com.google.common.collect.Sets;
import org.apache.bcel.util.ClassLoader;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelExecution;
import org.jage.gpu.binding.jocl.JoclGpu;
import org.jage.gpu.binding.jocl.arguments.DefaultJoclArgumentFactory;
import org.jage.gpu.binding.jocl.arguments.primitives.Int;
import org.jage.gpu.binding.jocl.kernelAsFunction.KernelAsFunctionJoclGpu;
import org.jage.gpu.binding.jocl.kernelAsFunction.SimpleGPU;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.GlobalArgument;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.PrimitiveWrapper;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class GpuExecutorTest {

    private File kernelSource = new File(ClassLoader.getSystemResource("simpleAddingTest.cl").getFile());
    private File kernelAsFunctionWithGlobal = new File(ClassLoader.getSystemResource("kernelAsFunctionWithGlobal.cl").getFile());
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
        kernelExecution.bindParameter(kernel.getArguments().get(0), 20);
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
        kernelExecution.bindParameter(kernel.getArguments().get(0), 20);
        kernelExecution.execute();
        for (int i = 0; i < 20; i++) {
            assertEquals(arrays[0][i] + arrays[1][i], arrays[2][i], 0.00001);
        }
    }

    @Test
    public void kernelAsFunctionWithGlobal() throws Exception {
        SimpleGPU joclGpu = new SimpleGPU();
        Kernel kernel = joclGpu.buildKernel(kernelAsFunctionWithGlobal, "adding");
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
        for (int i = 0; i < 2; i++) {
            kernelExecution.bindParameter(kernel.getArguments().get(i + 1), arrays[i]);
        }
        kernelExecution.bindParameter(kernel.getArguments().get(3), 10);
        kernelExecution.bindParameter(kernel.getArguments().get(4), arrays[2]);
        kernelExecution.bindParameter(kernel.getArguments().get(0), 20);
        kernelExecution.execute();
        for (int i = 0; i < 20; i++) {
            assertEquals((arrays[0][i] + arrays[1][i])*10, arrays[2][i], 0.00001);
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
        kernelExecution.bindParameter(kernel.getArguments().get(0), 20);
        kernelExecution.execute();
        for (int i = 0; i < 20; i++) {
            assertEquals(arrays[2][i], arrays[1][i], 0.00001);
        }
    }

}
@GlobalArgument
class GlobalInt extends PrimitiveWrapper<Integer> {
    public GlobalInt() {
        super(DefaultJoclArgumentFactory.INSTANCE.fromClass(Int.class));
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList("global_int");
    }
}