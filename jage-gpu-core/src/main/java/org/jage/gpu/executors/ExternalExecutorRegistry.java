package org.jage.gpu.executors;

public interface ExternalExecutorRegistry {
    ExternalExecutor get(String name);

    void flush();

}
