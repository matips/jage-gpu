#pragma OPENCL EXTENSION cl_khr_fp64 : enable

__kernel void simpleAddingTest(
    unsigned int height,
    __global double* a1,
    __global double* a2results
    )
{

    int globalIndex = get_global_id(0);
    if (globalIndex < height){
        a2results[globalIndex] = a1[globalIndex] + a2results[globalIndex] ;
    }
}
