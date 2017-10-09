package org.jage.gpu.binding.jocl.arguments.localMemory;

import com.google.common.collect.Sets;
import org.jage.gpu.binding.ArgumentAddressQualifier;
import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.AbstractJoclArgumentType;
import org.jage.gpu.binding.jocl.arguments.JoclKernelArgument;
import org.jocl.Sizeof;

import java.util.Set;

/**
 * CRIF IT Solutions Poland
 */
@JoclKernelArgument
public class GpuIntLocalMemory extends AbstractJoclArgumentType<Integer> {

    protected GpuIntLocalMemory() {
        super(Integer.class, "uint*", "int*");
    }

    @Override
    public void bind(Integer var, JOCLKernelExecution kernelExecution, org.jage.gpu.binding.KernelArgument kernelArgument) {
        kernelExecution.bindParameter(kernelArgument, null, (Sizeof.cl_int) * var);
    }

    @Override
    public Set<ArgumentAddressQualifier> validAddressSpaces() {
        return Sets.immutableEnumSet(ArgumentAddressQualifier.LOCAL);
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public ArgumentType toArray() {
        throw new RuntimeException("Cannot convert");
    }
}
