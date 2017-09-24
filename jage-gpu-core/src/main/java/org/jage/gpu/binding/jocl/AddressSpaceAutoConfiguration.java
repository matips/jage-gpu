package org.jage.gpu.binding.jocl;

import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.jage.gpu.binding.ArgumentAddressQualifier;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.FunctionArgumentFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Configure In/Out arguments by address space:
 * - global arrays are out arguments
 * - constant arrays are in parameters
 * - first parameter should be "int" refers to agents number
 */
public class AddressSpaceAutoConfiguration extends JoclGpu implements AutoConfigGPU {
    public AddressSpaceAutoConfiguration() throws IOException {
        super(true);
        setArgumentFactory(new FunctionArgumentFactory());
    }

    @Override
    public Kernel buildKernel(String kernelFileContent, String kernelName) throws IOException {
        JOCLKernel joclKernel = buildKernel(kernelFileContent, kernelName, Collections.emptySet(), Collections.emptySet());

        List<KernelArgument> arguments = joclKernel.getArguments();
        Set<String> outArguments = arguments.stream()
                .filter(kernelArgument -> kernelArgument.getAddressQualifier().equals(ArgumentAddressQualifier.GLOBAL))
                .map(KernelArgument::getArgumentName)
                .collect(Collectors.toSet());

        Set<String> inArguments = arguments.stream()
                .filter(kernelArgument -> Sets.newHashSet(ArgumentAddressQualifier.CONSTANT, ArgumentAddressQualifier.PRIVATE).contains(kernelArgument.getAddressQualifier()))
                .map(KernelArgument::getArgumentName)
                .collect(Collectors.toSet());
        return buildKernel(kernelFileContent, kernelName, inArguments, outArguments);

    }

    @Override
    public Kernel buildKernel(File sourceFile, String kernelName) throws IOException {
        return buildKernel(FileUtils.readFileToString(sourceFile), kernelName);
    }
}
