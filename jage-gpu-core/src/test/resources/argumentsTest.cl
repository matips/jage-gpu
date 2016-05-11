#pragma OPENCL EXTENSION cl_khr_fp64 : enable

__kernel void
sampleKernel(__global const volatile float *first,
             __constant char *second,
             __local unsigned int *third,
             unsigned short fourth,
             __constant unsigned int* fifth)
{
}
