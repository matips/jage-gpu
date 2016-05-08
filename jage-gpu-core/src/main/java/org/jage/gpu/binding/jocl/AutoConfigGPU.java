package org.jage.gpu.binding.jocl;

import java.io.File;
import java.io.IOException;

import org.jage.gpu.binding.Kernel;

/**
 * Generate Kernels without specifying in/out arguments.
 */
public interface AutoConfigGPU {
    Kernel buildKernel(String kernelFileContent, String kernelName) throws IOException;
    Kernel buildKernel(File sourceFile, String kernelName) throws IOException;
}
