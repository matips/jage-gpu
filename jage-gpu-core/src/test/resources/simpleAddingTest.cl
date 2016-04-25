
__kernel void adding(
    double a1,
    double a2,
    __global double* result
)
{
    *result = a1+a2;
}

__kernel void simpleAddingTest(
    unsigned int height,
    __global double* a1,
    __global double* a2,
    __global double* result
    )
{

    int globalIndex = get_global_id(0);
    if (globalIndex < height){
        adding(a1[globalIndex], a2[globalIndex], &result[globalIndex]);
    }
}
