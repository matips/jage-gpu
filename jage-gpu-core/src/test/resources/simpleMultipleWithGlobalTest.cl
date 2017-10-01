#pragma OPENCL EXTENSION cl_khr_fp64 : enable
#include "globalParams.cl"



__kernel void simpleMultipleTest(
    AgentsCount height,
    double a1,
    __global double* a2,
    __global double* result
    )
{

    int globalIndex = get_global_id(0);
    if (globalIndex < height){
        result[globalIndex] = a1 * a2[globalIndex];
    }
}
