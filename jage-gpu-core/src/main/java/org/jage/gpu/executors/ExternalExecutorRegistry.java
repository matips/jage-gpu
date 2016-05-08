package org.jage.gpu.executors;

/**
 * Manage external executors.
 */
public interface ExternalExecutorRegistry {
    /**
     *
     * @param name of gpu kernel.
     * @return For all agents calling #get ExternalExecutorRegistry should returns same ExternalExecutor
     */
    ExternalExecutor get(String name);

    /**
     * Execute computations
     */
    void flush();

}
