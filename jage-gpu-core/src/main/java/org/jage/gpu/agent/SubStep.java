package org.jage.gpu.agent;

@FunctionalInterface
public interface SubStep {
    void execute();
    default boolean canExecute(){
        return true;
    }
}
