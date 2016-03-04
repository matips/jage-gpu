package org.jage.gpu.binding;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public interface GPU {
    Kernel buildKernel(File sourceFile, String kernelName, Set<String> inArguments, Set<String> outArguments) throws IOException;
}
