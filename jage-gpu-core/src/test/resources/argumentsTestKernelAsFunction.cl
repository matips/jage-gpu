#pragma OPENCL EXTENSION cl_khr_fp64 : enable

__kernel void adding(
    double a1,
    double a2,
    __global double* result
)
{
    *result = a1+a2;
}



__kernel void multipleInts(
    unsigned int a1,
    unsigned int a2,
    __global unsigned int* result
)
{
    *result = a1*a2;
}


