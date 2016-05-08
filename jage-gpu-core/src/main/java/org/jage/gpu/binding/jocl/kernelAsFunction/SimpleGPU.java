package org.jage.gpu.binding.jocl.kernelAsFunction;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.AutoConfigGPU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Accept function as kernel @see org.jage.gpu.binding.jocl.kernelAsFunction.KernelAsFunctionJoclGpu
 * Assume that all pointers are OUT arguments, and value arguments are IN arguments.
 */
public class SimpleGPU extends KernelAsFunctionJoclGpu implements AutoConfigGPU {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleGPU.class);

    public SimpleGPU() throws IOException {
    }

    @Override
    public Kernel buildKernel(String kernelFileContent, String functionName) throws IOException {
        Kernel baseFunction = joclGpu.buildKernel(kernelFileContent, functionName, Collections.emptySet(), Collections.emptySet());
        List<KernelArgument> arguments = baseFunction.getArguments();
        Set<String> outArguments = arguments.stream()
                .filter(kernelArgument -> kernelArgument.getType().isPointer())
                .map(KernelArgument::getArgumentName)
                .collect(Collectors.toSet());
        Set<String> inArguments = arguments.stream()
                .filter(kernelArgument -> !kernelArgument.getType().isPointer())
                .map(KernelArgument::getArgumentName)
                .collect(Collectors.toSet());

        LOGGER.info("Auto detection of IN/OUT arguments for kernel {}.  In: {}.  Out: {}", functionName, inArguments, outArguments);
        return super.buildKernel(kernelFileContent, functionName, inArguments, outArguments);

    }

    @Override
    public Kernel buildKernel(File sourceFile, String functionName) throws IOException {
        return buildKernel(FileUtils.readFileToString(sourceFile), functionName);
    }
}
