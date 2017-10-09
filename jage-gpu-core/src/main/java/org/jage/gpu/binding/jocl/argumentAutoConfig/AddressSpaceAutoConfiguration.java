package org.jage.gpu.binding.jocl.argumentAutoConfig;

import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.AutoConfigGPU;
import org.jage.gpu.binding.jocl.JOCLKernel;
import org.jage.gpu.binding.jocl.JoclGpu;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jage.gpu.binding.ArgumentAddressQualifier.*;
import static org.jage.gpu.binding.ArgumentTypeQualifier.CONST;

/**
 * Configure In/Out arguments by address space:
 * - global arrays are out arguments
 * - constant arrays are in parameters
 * - first parameter should be "int" refers to agents number
 * - InOutDouble type is in/out variable
 */
public class AddressSpaceAutoConfiguration extends JoclGpu implements AutoConfigGPU {
    public AddressSpaceAutoConfiguration() throws IOException {
        super(true);
    }

    @Override
    public Kernel buildKernel(String kernelFileContent, String kernelName) throws IOException {
        JOCLKernel joclKernel = buildKernel(kernelFileContent, kernelName, Collections.emptySet(), Collections.emptySet());

        List<KernelArgument> arguments = joclKernel.getArguments();
        Set<String> outArguments = arguments.stream()
                .filter(kernelArgument -> kernelArgument.getAddressQualifier().equals(GLOBAL)
                        && !Sets.newHashSet(CONST).contains(kernelArgument.getArgumentTypeQualifier())
                )
                .map(KernelArgument::getArgumentName)
                .collect(Collectors.toSet());

        Set<String> inArguments = arguments.stream()
                .filter(kernelArgument ->
                        Sets.newHashSet(CONSTANT, PRIVATE).contains(kernelArgument.getAddressQualifier())
                                || Sets.newHashSet(CONST).contains(kernelArgument.getArgumentTypeQualifier())
                                || isAnnotatedAsInOut(kernelArgument.getType())
                )
                .map(KernelArgument::getArgumentName)
                .collect(Collectors.toSet());
        return buildKernel(kernelFileContent, kernelName, inArguments, outArguments);

    }

    private boolean isAnnotatedAsInOut(ArgumentType type) {
        return type.getClass().isAnnotationPresent(InOut.class);
    }

    @Override
    public Kernel buildKernel(File sourceFile, String kernelName) throws IOException {
        return buildKernel(FileUtils.readFileToString(sourceFile), kernelName);
    }
}
