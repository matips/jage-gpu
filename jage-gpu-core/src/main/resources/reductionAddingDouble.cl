#pragma OPENCL EXTENSION cl_khr_fp64 : enable


__kernel void addingDouble(
        unsigned int length,
        __local double* scratch,
        __constant double *buffer,
        __global double *result
) {

    int global_index = get_global_id(0);
    double accumulator = 0;
    // Loop sequentially over chunks of input vector

    scratch[get_local_id(0)] = 0;
    barrier(CLK_LOCAL_MEM_FENCE);

    while (global_index < length) {
        double element = buffer[global_index];
        accumulator = accumulator + element;
        global_index += get_global_size(0);
    }

    // Perform parallel reduction
    int local_index = get_local_id(0);
    scratch[local_index] = accumulator;
    barrier(CLK_LOCAL_MEM_FENCE);
    for (int offset = get_local_size(0) / 2;
         offset > 0;
         offset = offset / 2) {
        if (local_index < offset) {
            double other = scratch[local_index + offset];
            double mine = scratch[local_index];
            scratch[local_index] = mine + other;
        }
        barrier(CLK_LOCAL_MEM_FENCE);
    }
    if (local_index == 0) {
        result[get_group_id(0)] = scratch[0];
    }
//     Perform sequentially adding of groups effects.
    long resultCount = length / get_local_size(0) + ((length % get_local_size(0)) != 0 ? 1 : 0);
    barrier(CLK_GLOBAL_MEM_FENCE);
    if (get_global_id(0) == 0)
        for (int i = 1; i < resultCount; i++) {
            result[0] += result[i];
        }
}

