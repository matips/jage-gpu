package org.jage.gpu.workplace;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.bcel.util.ClassLoader;
import org.jage.address.agent.AgentAddress;
import org.jage.agent.AggregateActionService;
import org.jage.gpu.agent.SubStep;
import org.jage.gpu.agent.SubStepAgent;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.jocl.JoclGpu;
import org.jage.gpu.executors.ExternalExecutor;
import org.jage.gpu.executors.ExternalExecutorRegistry;
import org.jage.gpu.executors.GpuExecutorRegistry;
import org.jage.platform.component.exception.ComponentException;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

public class SubStepAgentsWorkplaceTest {
    private final static File kernelSource = new File(ClassLoader.getSystemResource("simpleAddingTest.cl").getFile());
    private Kernel kernel;

    @Before
    public void setUp() throws IOException {
        JoclGpu joclGpu = new JoclGpu();
        joclGpu.initialize();
        kernel = joclGpu.buildKernel(kernelSource, "simpleAddingTest", Sets.newHashSet("a1", "a2"), Sets.newHashSet("result"));

    }

    @Test
    public void integralTestsOnGpu() throws Exception {
        final int numberOfSteps = 40;
        GpuExecutorRegistry externalExecutorRegistry = new GpuExecutorRegistry();
        SubStepAgent[] subStepAgents = new SubStepAgent[20];
        double[] expectedResults = new double[20];
        double[] tempSum = new double[20];
        AtomicInteger stepExecution = new AtomicInteger();
        externalExecutorRegistry.setKernels(Collections.singletonList(kernel));
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            double initial = random.nextDouble();
            expectedResults[i] = initial;

            final int finalI = i;
            subStepAgents[i] = new SubStepAgent(mock(AgentAddress.class)) {
                ExternalExecutor simpleAddingOnGpu;

                @Override
                public void initialize(ExternalExecutorRegistry externalExecutorRegistry) {
                    super.initialize(externalExecutorRegistry);
                    simpleAddingOnGpu = getGpuStep("simpleAddingTest");
                }

                double sum = initial;

                @Override
                public void step() {
                    double next = random.nextDouble();
                    expectedResults[finalI] += next;
                    SubStep subStep = simpleAddingOnGpu.createStep()
                            .putArg(sum)
                            .putArg(next)
                            .build(gpuReader -> sum = tempSum[finalI] = gpuReader.readDouble());
                    postponeStep(subStep);
                    stepExecution.incrementAndGet();
                }

            };
            subStepAgents[i].initialize(externalExecutorRegistry);

        }
        SubStepAgentsWorkplace instance = new SubStepAgentsWorkplace(mock(AgentAddress.class)) {
            @Override
            public void init() throws ComponentException {
                setExternalExecutorRegistry(externalExecutorRegistry);
                Arrays.asList(subStepAgents).forEach(this::add);
                setActionService(mock(AggregateActionService.class));
            }

        };
        instance.setExternalExecutorRegistry(externalExecutorRegistry);
        instance.init();
        for (int i = 0; i < numberOfSteps; i++) {
            instance.step();
        }

        for (int i = 0; i < 20; i++) {
            assertEquals(expectedResults[i], tempSum[i], 1e-10);
        }
        assertEquals(numberOfSteps * 20, stepExecution.get());
        assertEquals(numberOfSteps, instance.getStep());
    }
}