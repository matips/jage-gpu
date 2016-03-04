package org.jage.gpu.binding;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.bcel.util.ClassLoader;
import org.jage.gpu.binding.jocl.JoclGpu;
import org.junit.Test;

import com.google.common.collect.Sets;

public class GPUTest {

    private File kernelSource = new File(ClassLoader.getSystemResource("argumentsTest.cl").getFile());

    @Test
    public void testBuildKernel() throws Exception {
        JoclGpu instance = new JoclGpu();
        instance.initialize();

        Kernel sampleKernel = instance.buildKernel(kernelSource, "sampleKernel", Sets.newHashSet("first", "fourth"), Sets.newHashSet("fifth", "fourth"));

        assertEquals("sampleKernel", sampleKernel.getKernelName());
        List<KernelArgument> arguments = sampleKernel.getArguments();
        assertEquals("first", arguments.get(0).getArgumentName());
        assertEquals("second", arguments.get(1).getArgumentName());
        assertEquals("third", arguments.get(2).getArgumentName());
        assertEquals("fourth", arguments.get(3).getArgumentName());
        assertEquals("fifth", arguments.get(4).getArgumentName());

        assertEquals(ArgumentAccessQualifier.NONE, arguments.get(0).getAccessQualifier());
        assertEquals(ArgumentAccessQualifier.NONE, arguments.get(1).getAccessQualifier());
        assertEquals(ArgumentAccessQualifier.NONE, arguments.get(2).getAccessQualifier());
        assertEquals(ArgumentAccessQualifier.NONE, arguments.get(3).getAccessQualifier());
        assertEquals(ArgumentAccessQualifier.WRITE_ONLY, arguments.get(4).getAccessQualifier());

        assertEquals(ArgumentType.FLOAT_ARRAY, arguments.get(0).getType());
        assertEquals(ArgumentType.CHAR_ARRAY, arguments.get(1).getType());
        assertEquals(ArgumentType.INT_ARRAY, arguments.get(2).getType());
        assertEquals(ArgumentType.SHORT, arguments.get(3).getType());
        assertEquals(ArgumentType.IMAGE_2D, arguments.get(4).getType());

        assertTrue(arguments.get(0).isIn());
        assertFalse(arguments.get(1).isIn());
        assertFalse(arguments.get(2).isIn());
        assertTrue(arguments.get(3).isIn());
        assertFalse(arguments.get(4).isIn());

        assertFalse(arguments.get(0).isOut());
        assertFalse(arguments.get(1).isOut());
        assertFalse(arguments.get(2).isOut());
        assertTrue(arguments.get(3).isOut());
        assertTrue(arguments.get(4).isOut());

    }

    @Test(expected = org.jocl.CLException.class)
    public void testInvalidShoutdown() throws Exception {
        JoclGpu instance = new JoclGpu();
        instance.initialize();

        instance.shutdown();
        instance.buildKernel(kernelSource, "sampleKernel", Collections.emptySet(), Collections.emptySet());
    }
}