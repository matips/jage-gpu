#pragma OPENCL EXTENSION cl_khr_fp64 : enable

#include "random.cl"

__kernel void random_test(
    Random random,
    __global double* result
    )
{
    *result = nextDouble(&random);
}