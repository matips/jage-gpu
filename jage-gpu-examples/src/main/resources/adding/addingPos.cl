#pragma OPENCL EXTENSION cl_khr_fp64 : enable

__kernel void addingPos(
    unsigned int height,
    __global double* current_sum,
    __global double* position1,
    __global double* position2,
    __global double* step,
    __global double* result
    )
{
    int globalIndex = get_global_id(0);
    if (globalIndex < height){
        result[globalIndex] = current_sum[globalIndex] + (position1[globalIndex] + position2[globalIndex])* step[globalIndex] ;
    }
}
