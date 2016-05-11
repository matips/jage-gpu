package org.jage.gpu.binding.jocl.kernelAsFunction;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.apache.bcel.util.ClassLoader;
import org.jage.gpu.binding.ArgumentAccessQualifier;
import org.jage.gpu.binding.jocl.JoclArgumentTypes;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelArgument;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;

public class KernelAsFunctionGPUTest {

    private File kernelSource = new File(ClassLoader.getSystemResource("argumentsTestKernelAsFunction.cl").getFile());

    @Test
    public void testBuildKernel() throws Exception {
        KernelAsFunctionJoclGpu instance = new KernelAsFunctionJoclGpu();

        Kernel sampleKernel = instance.buildKernel(kernelSource, "adding", Sets.newHashSet("a1", "a2"), Sets.newHashSet("result"));

        assertEquals("generated_kernel_for_adding", sampleKernel.getKernelName());
        List<KernelArgument> arguments = sampleKernel.getArguments();
        assertEquals("height", arguments.get(0).getArgumentName());
        assertEquals("a1", arguments.get(1).getArgumentName());
        assertEquals("a2", arguments.get(2).getArgumentName());
        assertEquals("result", arguments.get(3).getArgumentName());

        for (int i = 0; i < 4; i++) {
            Assert.assertEquals(ArgumentAccessQualifier.NONE, arguments.get(i).getAccessQualifier());
        }
        Assert.assertEquals(JoclArgumentTypes.INT, arguments.get(0).getType());
        for (int i = 1; i < 4; i++) {
            assertEquals(JoclArgumentTypes.DOUBLE_ARRAY, arguments.get(i).getType());
        }

        assertFalse(arguments.get(0).isIn());
        assertTrue(arguments.get(1).isIn());
        assertTrue(arguments.get(2).isIn());
        assertTrue(arguments.get(3).isOut());
    }

}