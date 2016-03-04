package org.jage.gpu;

import org.jage.gpu.agent.SubStep;

public interface ExternalStepBuilder {

    ExternalStepBuilder putArg(double argument);

    SubStep build(KernelCallBack callBack);
}
