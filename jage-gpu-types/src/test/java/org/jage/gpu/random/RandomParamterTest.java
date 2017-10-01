package org.jage.gpu.random;

import com.google.common.collect.Sets;
import org.apache.bcel.util.ClassLoader;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelExecution;
import org.jage.gpu.binding.jocl.kernelAsFunction.KernelAsFunctionJoclGpu;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class RandomParamterTest {

    private File kernelSource = new File(ClassLoader.getSystemResource("randomAC.cl").getFile());

    @Test
    public void testBuildKernel() throws Exception {
        KernelAsFunctionJoclGpu instance = new KernelAsFunctionJoclGpu();

        Kernel kernel = instance.buildKernel(kernelSource, "random_test", Sets.newHashSet("a1", "a2"), Sets.newHashSet("result"));

        KernelExecution kernelExecution = kernel.newExecution(20);
        Random random = new Random();
        double[] resultArray = new double[20];

        kernelExecution.bindParameter(kernel.getArguments().get(0), 20);
        kernelExecution.bindParameter(kernel.getArguments().get(1), random);
        kernelExecution.bindParameter(kernel.getArguments().get(2), resultArray);
        kernelExecution.execute();

        double[] sorted = Arrays.stream(resultArray)
                .sorted()
                .toArray();
        double[] sortedReverse = Arrays.stream(resultArray)
                .mapToObj(a -> a)
                .sorted(Comparator.reverseOrder())
                .mapToDouble(a -> a)
                .toArray();
        long distictValues = Arrays.stream(resultArray)
                .distinct()
                .count();

        assertThat(sorted).isNotEqualTo(resultArray);
        assertThat(sortedReverse).isNotEqualTo(resultArray);
        assertThat(distictValues).isEqualTo(resultArray.length);
    }

}