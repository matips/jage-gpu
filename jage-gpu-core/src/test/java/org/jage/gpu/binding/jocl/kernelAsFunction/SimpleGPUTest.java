package org.jage.gpu.binding.jocl.kernelAsFunction;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.apache.bcel.util.ClassLoader;
import org.jage.gpu.binding.ArgumentAccessQualifier;
import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.AutoConfigGPU;
import org.junit.Test;

public class SimpleGPUTest {
    private File kernelSource = new File(ClassLoader.getSystemResource("argumentsTestKernelAsFunction.cl").getFile());


    @Test
    public void buildKernel() throws Exception {
        AutoConfigGPU instance = new SimpleGPU();

        Kernel sampleKernel = instance.buildKernel(kernelSource, "adding");

        assertEquals("generated_kernel_for_adding", sampleKernel.getKernelName());
        List<KernelArgument> arguments = sampleKernel.getArguments();
        assertEquals("height", arguments.get(0).getArgumentName());
        assertEquals("a1", arguments.get(1).getArgumentName());
        assertTrue(arguments.get(1).isIn());
        assertFalse(arguments.get(1).isOut());
        assertEquals("a2", arguments.get(2).getArgumentName());
        assertTrue(arguments.get(2).isIn());
        assertFalse(arguments.get(2).isOut());
        assertEquals("result", arguments.get(3).getArgumentName());
        assertFalse(arguments.get(3).isIn());
        assertTrue(arguments.get(3).isOut());

        for (int i = 0; i < 4; i++) {
            assertEquals(ArgumentAccessQualifier.NONE, arguments.get(i).getAccessQualifier());
        }
        assertEquals(ArgumentType.INT, arguments.get(0).getType());
        for (int i = 1; i < 4; i++) {
            assertEquals(ArgumentType.DOUBLE_ARRAY, arguments.get(i).getType());
        }

        assertFalse(arguments.get(0).isIn());
        assertTrue(arguments.get(1).isIn());
        assertTrue(arguments.get(2).isIn());
        assertTrue(arguments.get(3).isOut());
    }

}