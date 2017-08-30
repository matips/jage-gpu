#pragma OPENCL EXTENSION cl_khr_fp64 : enable

typedef int global_int;

__kernel void adding(
    double a1,
    double a2,
    global_int a3,
    __global double* result
)
{
    *result = (a1+a2)*a3;
}



