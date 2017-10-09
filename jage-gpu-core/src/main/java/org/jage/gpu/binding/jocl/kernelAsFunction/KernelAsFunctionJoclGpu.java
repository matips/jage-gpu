package org.jage.gpu.binding.jocl.kernelAsFunction;

import org.apache.commons.io.FileUtils;
import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.GPU;
import org.jage.gpu.binding.Kernel;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JoclGpu;
import org.jage.gpu.binding.jocl.arguments.JoclArgumentType;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.FunctionArgumentType;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.GlobalArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * This implementation of GPU accepts functions sources witch operated on one data row.
 * It wraps function to multirow kernel.
 * <p>
 * WARNING: Passing arrays is not supported!
 * </p>
 */
public class KernelAsFunctionJoclGpu implements GPU {
    private static final Logger LOGGER = LoggerFactory.getLogger(KernelAsFunctionJoclGpu.class);
    protected final JoclGpu joclGpu;
    private static final String KERNEL_TEMPLATE = "__kernel void %s(\n"
            + "%s\n"
            + "    )\n"
            + "{\n"
            + "\n"
            + "    int globalIndex = get_global_id(0);\n"
            + "    %s"
            + "    if (globalIndex < height){\n"
            + "        %s(%s);\n"
            + "    }\n"
            + "}";

    public KernelAsFunctionJoclGpu() throws IOException {
        joclGpu = new JoclGpu(true);
    }

    @Override
    public Kernel buildKernel(File sourceFile, String functionName, Set<String> inArguments, Set<String> outArguments)
            throws IOException {
        // Create the program from the source code

        String programSource = FileUtils.readFileToString(sourceFile);
        return buildKernel(programSource, functionName, inArguments, outArguments);
    }

    @Override
    public Kernel buildKernel(String kernelFileContent, String functionName, Set<String> inArguments, Set<String> outArguments) throws IOException {
        Kernel baseFunction = joclGpu.buildKernel(kernelFileContent, functionName, inArguments, outArguments);

        String wrapper = generateWrapper(functionName, baseFunction);
        String fullKernelSource = kernelFileContent + "\n\n\n "
                + "//#### FOLLOWING KERNEL WAS GENERATED BY JAGE-GPU #####"
                + "\n\n"
                + wrapper;

        LOGGER.info("Generated kernel for {} is:\n {}", functionName, fullKernelSource);
        return joclGpu.buildKernel(fullKernelSource, generatedKernelName(functionName), inArguments, outArguments);
    }

    private String generatedKernelName(String functionName) {
        return "generated_kernel_for_" + functionName;
    }

    private String generateWrapper(String functionName, Kernel baseFunction) {

        String kernelArgumentsText = "AgentsCount height";
        String functionCallArguments = "";

        for (KernelArgument functionArgument : baseFunction.getArguments()) {
            kernelArgumentsText += ",\n";
            ArgumentType argumentType = toWrapperType(functionArgument.getType());
            if (argumentType.isPointer()) {
                kernelArgumentsText += "__global ";
            }
            kernelArgumentsText += ((JoclArgumentType) argumentType).getCNames().get(0);
            kernelArgumentsText += " ";
            kernelArgumentsText += functionArgument.getArgumentName();

            if (functionCallArguments.length() > 0) {
                functionCallArguments += ",";
            }
            if (functionArgument.isOut()) {
                functionCallArguments += "&";
            }
            functionCallArguments += getFunctionParametrName(functionArgument);
            if (argumentType.isArray()) {
                functionCallArguments += "[globalIndex]";
            }
        }
        return String.format(KERNEL_TEMPLATE, generatedKernelName(functionName), kernelArgumentsText, generatePreExecutionBlock(baseFunction), functionName,
                functionCallArguments);
    }

    //todo: refactor to remove instanceof
    private String getFunctionParametrName(KernelArgument functionArgument) {
        ArgumentType type = functionArgument.getType();
        if (type instanceof FunctionArgumentType<?>) {
            return ((FunctionArgumentType) type).toSubname(functionArgument);
        } else {
            return functionArgument.getArgumentName();
        }
    }

    private ArgumentType toWrapperType(ArgumentType type) {
        if (type.getClass().isAnnotationPresent(GlobalArgument.class)) {
            return type;
        } else if (type instanceof FunctionArgumentType<?>) {
            return ((FunctionArgumentType) type).toWrapperType();
        } else {
            return (type.isPointer()) ? type : type.toArray();
        }
    }

    private String generatePreExecutionBlock(Kernel baseFunction) {
        String preExecutionBlock = "\n\t//### preExectionBlock\n\t";
        for (KernelArgument functionArgument : baseFunction.getArguments()) {
            ArgumentType type = functionArgument.getType();
            if (type instanceof FunctionArgumentType<?>) {
                FunctionArgumentType globalArgument = (FunctionArgumentType) type;
                preExecutionBlock += globalArgument.preExecutionBlock(functionArgument).replace("\n", "\n\t");
            }
        }
        return preExecutionBlock.trim() + "\n\t//### preExectionBlock end \n\n";

    }

}
