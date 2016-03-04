package org.jage.gpu.agent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.jage.address.agent.AgentAddress;
import org.jage.gpu.executors.ExternalExecutor;
import org.jage.gpu.executors.ExternalExecutorRegistry;
import org.junit.Test;

public class SubStepAgentTest {
    @Test
    public void testResume() throws Exception {
        ExternalExecutorRegistry externalExecutorRegistryMock = mock(ExternalExecutorRegistry.class);
        SubStep subStepMock = mock(SubStep.class);

        when(subStepMock.canExecute()).thenReturn(false);
        SubStepAgent subStepAgent = new SubStepAgent(mock(AgentAddress.class)) {
            @Override
            public void step() {
                postponeStep(subStepMock);
            }
        };
        subStepAgent.initialize(externalExecutorRegistryMock);
        subStepAgent.step();
        assertFalse(subStepAgent.resume());
        assertFalse(subStepAgent.resume());
        verify(subStepMock, never()).execute();

        when(subStepMock.canExecute()).thenReturn(true);

        //always return true
        assertTrue(subStepAgent.resume());
        assertTrue(subStepAgent.resume());
        assertTrue(subStepAgent.resume());
        assertTrue(subStepAgent.resume());

        //but execute call only once
        verify(subStepMock).execute();

    }
    @Test
    public void testResumeImmidietly() throws Exception {
        ExternalExecutorRegistry externalExecutorRegistryMock = mock(ExternalExecutorRegistry.class);
        SubStep subStepMock = mock(SubStep.class);

        when(subStepMock.canExecute()).thenReturn(true);
        SubStepAgent subStepAgent = new SubStepAgent(mock(AgentAddress.class)) {
            @Override
            public void step() {
                postponeStep(subStepMock);
            }
        };
        subStepAgent.initialize(externalExecutorRegistryMock);
        subStepAgent.step();
        verify(subStepMock).execute();
        assertTrue(subStepAgent.resume());


    }
    @Test
    public void testGetGpuStep() throws Exception {
        ExternalExecutorRegistry externalExecutorRegistryMock = mock(ExternalExecutorRegistry.class);
        ExternalExecutor gpuExecutorMock = mock(ExternalExecutor.class);
        when(externalExecutorRegistryMock.get("testName")).thenReturn(gpuExecutorMock);

        SubStepAgent subStepAgent = new SubStepAgent(mock(AgentAddress.class)) {
            @Override
            public void step() {
                ExternalExecutor gpuExecutor = getGpuStep("testName");
                assertEquals(gpuExecutorMock, gpuExecutor);
            }
        };
        subStepAgent.initialize(externalExecutorRegistryMock);
        subStepAgent.step();
        verify(externalExecutorRegistryMock).get("testName");


    }
}