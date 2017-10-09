package org.jage.gpu.random;

import org.jage.gpu.binding.ArgumentAddressQualifier;
import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.JoclKernelArgument;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.FunctionArgumentType;
import org.jage.gpu.binding.jocl.kernelAsFunction.arguments.GlobalArgument;
import org.jocl.Pointer;
import org.jocl.Sizeof;

import java.util.*;

@GlobalArgument
@JoclKernelArgument
public class GpuRandomType implements FunctionArgumentType<Random> {
    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isPointer() {
        return false;
    }

    @Override
    public ArgumentType toArray() {
        throw new RuntimeException("Not implemented yet.");
    }

    @Override
    public boolean is(Class javaType) {
        return Random.class.equals(javaType);
    }


    @Override
    public void bind(Random var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        kernelExecution.bindParameter(kernelArgument, Pointer.to(new long[]{var.nextLong()}), Sizeof.cl_long);
    }

    @Override
    public List<String> getCNames() {
        return Collections.singletonList("Random");
    }

    @Override
    public Set<ArgumentAddressQualifier> validAddressSpaces() {
        return EnumSet.allOf(ArgumentAddressQualifier.class);
    }

    @Override
    public String preExecutionBlock(KernelArgument argumentName) {
        String subname = toSubname(argumentName);
        return "Random " + subname + ";\n" +
                "if (globalIndex < height){\n" +
                "\t" + subname + ".seed =  " + argumentName.getArgumentName() + ".seed * get_global_id(0);\n" +
                "}\n";
    }

    @Override
    public String toSubname(KernelArgument kernelArgument) {
        return kernelArgument.getArgumentName() + "_local";
    }

    @Override
    public ArgumentType toWrapperType() {
        return this;
    }
}