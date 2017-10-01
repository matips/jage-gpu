#pragma OPENCL EXTENSION cl_khr_fp64 : enable
#include "globalParams.cl"

__kernel void adding(
    double a1,
    double a2,
    __global double* result
)
{
    *result = a1+a2;
}

__kernel void simpleAddingTest(
    AgentsCount height,
    __global double* a1,
    __global double* a2,
    __global double* result
    )
{

    int globalIndex = get_global_id(0);
    if (globalIndex < height){
        adding(a1[globalIndex], a2[globalIndex], &result[globalIndex]);
    }
}

__kernel void twoLevelTests(
    AgentsCount height,
    __global double* a1,
    __global double* a2,
    __global int* l2referce,

    AgentsCount level2,
    __global double* result,
    __global double* a3

    )
{

    int globalIndex = get_global_id(0);
    int gi = globalIndex;
    if (gi < height){
        result[gi] = a1[gi] + a2[gi] + a3[l2referce[gi]];
    }
}
