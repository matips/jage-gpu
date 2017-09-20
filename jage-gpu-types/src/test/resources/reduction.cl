#pragma OPENCL EXTENSION cl_khr_fp64 : enable

#include "localArrays.cl"
#include "globalPrimitives.cl"

__kernel void sum(
    LocalIntArray localArray,
    __global int* result
    )
{
    *result = 14;
}
