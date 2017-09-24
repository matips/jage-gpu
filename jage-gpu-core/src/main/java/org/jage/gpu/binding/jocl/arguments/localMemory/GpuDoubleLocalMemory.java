package org.jage.gpu.binding.jocl.arguments.localMemory;

import com.google.common.collect.Sets;
import org.jage.gpu.binding.ArgumentAddressQualifier;
import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.AbstractJoclArgumentType;
import org.jage.gpu.binding.jocl.arguments.PrimitiveArgument;
import org.jocl.Sizeof;

import java.util.Set;

/**
 * CRIF IT Solutions Poland
 */
@PrimitiveArgument
public class GpuDoubleLocalMemory extends AbstractJoclArgumentType<Integer> {

    protected GpuDoubleLocalMemory() {
        super(Integer.class, "double*");
    }

    @Override
    public void bind(Integer var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        kernelExecution.bindParameter(kernelArgument, null, (Sizeof.cl_double) * var);
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
