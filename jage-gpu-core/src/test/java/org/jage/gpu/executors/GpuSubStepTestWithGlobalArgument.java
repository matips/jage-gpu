package org.jage.gpu.executors;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.bcel.util.ClassLoader;
import org.jage.gpu.agent.SubStep;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.jocl.JoclGpu;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

public class GpuSubStepTestWithGlobalArgument {
    private final static File kernelSource = new File(ClassLoader.getSystemResource("simpleMultipleWithGlobalTest.cl").getFile());
    private GpuExecutor instance;

    @Before
    public void setUp() throws IOException {
        JoclGpu joclGpu = new JoclGpu();
        joclGpu.initialize();
        Kernel kernel = joclGpu.buildKernel(kernelSource, "simpleMultipleTest", Sets.newHashSet("a1", "a2"), Sets.newHashSet("result"));

        instance = new GpuExecutor(kernel);
    }

    @Test
    public void testFlush() throws Exception {
        Random random = new Random();
        AtomicInteger callackCalls = new AtomicInteger();
        List<SubStep> subSteps = new ArrayList<>();
        double a1 = random.nextDouble();
        for (int i = 0; i < 40; i++) {
            double a2 = random.nextDouble();
            subSteps.add(instance.createStep()
                    .putArg(a2)
                    .build(gpuReader -> {
                        assertEquals("Different on " + callackCalls.get() + " comparation", a1 * a2, gpuReader.readDouble(), 1e-20);
                        callackCalls.incrementAndGet();
                    }));
        }

        subSteps.stream().map(SubStep::canExecute).forEach(org.junit.Assert::assertFalse);
        instance.bindGlobalArgument(a1);
        instance.flush();
        subSteps.stream().map(SubStep::canExecute).forEach(org.junit.Assert::assertTrue);
    }
}