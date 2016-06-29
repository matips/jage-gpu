package org.jage.gpu.binding;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.bcel.util.ClassLoader;
import org.jage.gpu.binding.jocl.JoclGpu;
import org.jage.gpu.binding.jocl.arguments.DefaultJoclArgumentFactory;
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
        assertEquals(ArgumentAccessQualifier.NONE, arguments.get(4).getAccessQualifier());

        assertEquals(DefaultJoclArgumentFactory.INSTANCE.fromClass(float[].class), arguments.get(0).getType());
        assertEquals(DefaultJoclArgumentFactory.INSTANCE.fromClass(char[].class), arguments.get(1).getType());
        assertEquals(DefaultJoclArgumentFactory.INSTANCE.fromClass(int[].class), arguments.get(2).getType());
        assertEquals(DefaultJoclArgumentFactory.INSTANCE.fromClass(Short.class), arguments.get(3).getType());
        assertEquals(DefaultJoclArgumentFactory.INSTANCE.fromClass(int[].class), arguments.get(4).getType());

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
    public void testInvalidKernelName() throws IOException {
        JoclGpu instance = new JoclGpu(true);

        instance.buildKernel(kernelSource, "invalidKernelName", Sets.newHashSet("first", "fourth"), Sets.newHashSet("fifth", "fourth"));

    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidShoutdown() throws Exception {
        JoclGpu instance = new JoclGpu();
        instance.initialize();

        instance.shutdown();
        instance.buildKernel(kernelSource, "sampleKernel", Collections.emptySet(), Collections.emptySet());
    }
}