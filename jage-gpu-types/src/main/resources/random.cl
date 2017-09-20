

#define DOUBLE_UNIT 0x1.0p-53
#define MASK (1L << 48) - 1;

typedef struct Random_t{
    long seed;
};

typedef struct Random_t Random;


int next(int bits, Random* random) {
    long oldseed = random->seed;
    long nextseed = (oldseed * 0x5DEECE66DL + 0xBL) & MASK;
    random->seed = nextseed;

    return (int)(nextseed >> (48 - bits));
}

double nextDouble(Random* random) {
    return (((long)(next(26, random)) << 27) + next(27, random)) * DOUBLE_UNIT;
}

