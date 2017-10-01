package org.jage.gpu.binding;

import java.util.List;

public interface Kernel {
    String getKernelName();

    List<KernelArgument> getArguments();

    KernelExecution newExecution(int globalWorkers);
}
