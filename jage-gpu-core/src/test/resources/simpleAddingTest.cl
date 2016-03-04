__kernel void simpleAddingTest(
    unsigned int height,
    __global double* a1,
    __global double* a2,
    __global  double* result
    )
{

    int globalIndex = get_global_id(0);
    if (globalIndex < height){
        result[globalIndex] = a1[globalIndex] + a2[globalIndex] ;
    }
}
