#pragma OPENCL EXTENSION cl_khr_fp64 : enable

__kernel void integral(
    unsigned int height,
    __global double* left_bound,
    __global double* right_bound,
    __global double* result
    )
{
    int globalIndex = get_global_id(0);
    if (globalIndex < height){
        result[globalIndex] = 0 ;
        double h = 0.0001;
        for (double y = left_bound[globalIndex]; y < right_bound[globalIndex]; y += h ){
            result[globalIndex] += sin(y);
        }
        result[globalIndex] *= h;
    }
}
