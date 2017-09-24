#pragma OPENCL EXTENSION cl_khr_fp64 : enable
#include "random.cl"

__kernel void multipleByRandom(
    unsigned int height,
    Random randomParam,
    __constant double* a2,
    __global double* result
    )
{
    int globalIndex = get_global_id(0);

    Random random;
    random.seed = randomParam.seed * globalIndex;

    if (globalIndex < height){
        result[globalIndex] = nextDouble(&random) * a2[globalIndex];
    }
}
