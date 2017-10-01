package org.jage.gpu;

import org.jage.gpu.agent.SubStep;

/**
 * CRIF IT Solutions Poland
 */
public interface Resumable {
    void postponeStep(SubStep step);

    boolean resume();
}
