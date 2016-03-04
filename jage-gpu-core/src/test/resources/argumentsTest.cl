__kernel void
sampleKernel(__global const volatile float *first,
             __constant char *second,
             __local unsigned int *third,
             unsigned short fourth,
             __write_only image2d_t fifth)
{
}
