#pragma OPENCL EXTENSION cl_khr_fp64 : enable

__kernel void integral(
    double left_bound,
    double right_bound,
    __global double* result
    )
{
    double result_tmp = 0 ;
    double h = 0.0001;
    for (double y = left_bound; y < right_bound; y += h ){
        result_tmp += sin(y);
    }
    *result *= h;
}
