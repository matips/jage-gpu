package org.jage.gpu.executors;

import com.google.common.collect.Sets;
import org.apache.bcel.util.ClassLoader;
import org.jage.gpu.agent.SubStep;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.jocl.JoclGpu;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class GpuSubStepTest {
    private final static File kernelSource = new File(ClassLoader.getSystemResource("simpleAddingTest.cl").getFile());
    private JoclGpu joclGpu;

    @Before
    public void setUp() throws IOException {
        joclGpu = new JoclGpu();
        joclGpu.initialize();
    }

    @Test
    public void testFlush() throws Exception {
        Kernel kernel = joclGpu.buildKernel(kernelSource, "simpleAddingTest", Sets.newHashSet("a1", "a2"), Sets.newHashSet("result"));
        GpuExecutor instance = new GpuExecutor(kernel);

        Random random = new Random();
        AtomicInteger callackCalls = new AtomicInteger();
        List<SubStep> subSteps = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            double a1 = random.nextDouble();
            double a2 = random.nextDouble();
            subSteps.add(instance.createStep(0)
                    .putArg(a1)
                    .putArg(a2)
                    .build(gpuReader -> {
                        assertEquals("Different on " + callackCalls.get() + " comparation", a1 + a2, gpuReader.readDouble(), 1e-20);
                        callackCalls.incrementAndGet();
                    }));

        }

        subSteps.stream().map(SubStep::canExecute).forEach(org.junit.Assert::assertFalse);
        instance.flush();
        subSteps.stream().map(SubStep::canExecute).forEach(org.junit.Assert::assertTrue);
    }

    @Test
    public void twoLevels() throws Exception {
        Kernel kernel = joclGpu.buildKernel(kernelSource, "twoLevelTests", Sets.newHashSet("a1", "a2", "a3", "l2referce"), Sets.newHashSet("result"));
        GpuExecutor instance = new GpuExecutor(kernel);

        Random random = new Random();
        AtomicInteger callackCalls = new AtomicInteger();
        List<SubStep> subSteps = new ArrayList<>();
        instance.createStep(1)
                .putArg(31.7)
                .build(reader -> {
                });
        for (int i = 0; i < 40; i++) {
            double a1 = random.nextDouble();
            double a2 = random.nextDouble();
            subSteps.add(instance.createStep(0)
                    .putArg(a1)
                    .putArg(a2)
                    .putIndex(1, -1)
                    .build(gpuReader -> {
                        int id = callackCalls.get();
                        assertEquals("Different on " + id + " comparation", a1 + a2 + 31.7, gpuReader.readDouble(), 1e-20);
                        callackCalls.incrementAndGet();
                    }));

        }

        subSteps.stream().map(SubStep::canExecute).forEach(org.junit.Assert::assertFalse);
        instance.flush();
        subSteps.stream().map(SubStep::canExecute).forEach(org.junit.Assert::assertTrue);
        subSteps.stream().forEach(SubStep::execute);
    }
}


