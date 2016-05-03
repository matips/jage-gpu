#pragma OPENCL EXTENSION cl_khr_fp64 : enable

__kernel void adding(
    double a1,
    double a2,
    __global double* result
)
{
    *result = a1+a2;
}


