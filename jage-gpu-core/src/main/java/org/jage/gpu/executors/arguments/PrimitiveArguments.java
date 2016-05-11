package org.jage.gpu.executors.arguments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jage.gpu.binding.KernelArgument;

abstract class PrimitiveArguments<T, V> {
    final private List<KernelArgument> arguments;
    final private Map<KernelArgument, T> kernelArgumentToList = new HashMap<>();
    private final Function<T, V> toArray;
    private final Function<Integer, V> internalArrayConstructor;

    protected PrimitiveArguments(List<KernelArgument> arguments, Class type, Function<Integer, T[]> arrayConstructor, Supplier<T> arrayListConstructor, Function<T, V> toArray, Function<Integer, V> internalArrayConstructor) {
        this.toArray = toArray;
        this.internalArrayConstructor = internalArrayConstructor;
        this.arguments = arguments.stream()
                .filter(argument -> argument.getType().is(type))
                .collect(Collectors.toList());

        T[] inArray = createInArray(arrayConstructor);
        initializeArrays(inArray, arrayListConstructor);
        setIn(inArray);
    }

    private V getArray(int gpuArgumentsNumber, KernelArgument argument) {
        return Optional.ofNullable(kernelArgumentToList.get(argument))
                .map(toArray)
                .orElseGet(() -> internalArrayConstructor.apply(gpuArgumentsNumber));

    }

    public Map<KernelArgument, V> getArrays(int gpuArgumentsNumber) {
        return arguments.stream()
                .collect(Collectors.toMap(Function.identity(), argument -> getArray(gpuArgumentsNumber, argument)));
    }

    protected int countIn() {
        return (int) arguments.stream()
                .filter(KernelArgument::isIn)
                .count();
    }

    protected T[] createInArray(Function<Integer, T[]> arrayConstructor) {
        int inSize = countIn();
        return arrayConstructor.apply(inSize);
    }

    protected void initializeArrays(T[] in, Supplier<T> arrayListConstructor) {
        AtomicInteger inIndex = new AtomicInteger();
        arguments.stream()
                .filter(KernelArgument::isIn)
                .forEachOrdered(argument -> {
                    T arrayList = arrayListConstructor.get();
                    kernelArgumentToList.put(argument, arrayList);
                    in[inIndex.getAndIncrement()] = arrayList;
                });
    }

    protected abstract void setIn(T[] inArray);
}