
__kernel void adding(
    double a1,
    double a2,
    __global double* result
)
{
    *result = a1+a2;
}


