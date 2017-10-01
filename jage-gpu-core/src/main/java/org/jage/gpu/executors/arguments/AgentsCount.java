package org.jage.gpu.executors.arguments;

import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.KernelArgument;
import org.jage.gpu.binding.jocl.JOCLKernelExecution;
import org.jage.gpu.binding.jocl.arguments.DefaultJoclArgumentFactory;
import org.jage.gpu.binding.jocl.arguments.PrimitiveArgument;
import org.jage.gpu.binding.jocl.arguments.arrays.IntArray;
import org.jage.gpu.binding.jocl.arguments.primitives.JoclPrimitiveType;
import org.jocl.Pointer;
import org.jocl.Sizeof;

@PrimitiveArgument
public class AgentsCount extends JoclPrimitiveType<Integer> {

    public static final String AGENTS_COUNT = "AgentsCount";

    AgentsCount() {
        super(Integer.class, AGENTS_COUNT);
    }

    @Override
    public void bind(Integer var, JOCLKernelExecution kernelExecution, KernelArgument kernelArgument) {
        kernelExecution.bindParameter(kernelArgument, Pointer.to(new int[] { var }), Sizeof.cl_uint);
    }

    @Override
    public ArgumentType toArray() {
        return DefaultJoclArgumentFactory.INSTANCE.fromClass(IntArray.class);
    }
}