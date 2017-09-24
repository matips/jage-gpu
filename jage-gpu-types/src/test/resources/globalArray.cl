#pragma OPENCL EXTENSION cl_khr_fp64 : enable

#include "localArrays.cl"
#include "globalPrimitives.cl"

__kernel void globalArray(
    GlobalDoubleArray localArray,
    __global int* result
    )
{
    *result = 14;
}
