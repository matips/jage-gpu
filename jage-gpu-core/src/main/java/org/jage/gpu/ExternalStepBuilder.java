package org.jage.gpu;

import org.jage.gpu.agent.SubStep;

public interface ExternalStepBuilder {

    ExternalStepBuilder putArg(double argument);

    ExternalStepBuilder putArg(int argument);

    SubStep build(KernelCallBack callBack);
}
