package org.jage.gpu;

import org.jage.gpu.agent.SubStep;

public interface ExternalStepBuilder {

    ExternalStepBuilder putArg(double argument);

    ExternalStepBuilder putArg(int argument);

    //diff is need for shift index according to call order on diffrent levels
    ExternalStepBuilder putIndex(int level, int diff);

    SubStep build(KernelCallBack callBack);
}
