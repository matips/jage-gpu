package org.jage.gpu.binding.jocl.kernelAsFunction.arguments;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jage.gpu.binding.jocl.arguments.DefaultJoclArgumentFactory;
import org.jage.gpu.binding.jocl.arguments.JoclArgumentFactory;
import org.jage.gpu.binding.jocl.arguments.JoclArgumentType;
import org.jage.gpu.helpers.ThrowingFunction;
import org.reflections.Reflections;

public class FunctionArgumentFactory implements JoclArgumentFactory {
    JoclArgumentFactory baseFactory = DefaultJoclArgumentFactory.INSTANCE;
    private List<FunctionArgumentType> arguments;

    public void init() {
        Reflections reflections = new Reflections("");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(GlobalArgument.class);
        arguments = annotated.stream()
                .map(aClass -> {
                    if (FunctionArgumentType.class.isAssignableFrom(aClass))
                        return (Class<? extends FunctionArgumentType>) aClass;
                    throw new RuntimeException(aClass.getName() + " is annotated with " + GlobalArgument.class.getName());
                })
                .map((ThrowingFunction<Class<? extends FunctionArgumentType>, Constructor<? extends FunctionArgumentType>>) aClass -> aClass
                        .getDeclaredConstructor())
                .peek(constructor -> constructor.setAccessible(true))
                .map((ThrowingFunction<Constructor<? extends FunctionArgumentType>, FunctionArgumentType>) constructor -> constructor.newInstance())
                .collect(Collectors.toList());
    }

    @Override
    public FunctionArgumentType fromName(String cTypeName) {
        if (arguments == null)
            init();

        return arguments.stream()
                .filter(type -> type.getNames().contains(cTypeName.trim()))
                .findAny()
                .orElseGet(() -> {
                    JoclArgumentType joclArgumentType = baseFactory.fromName(cTypeName);
                    return new PrimitiveWrapper(joclArgumentType);
                });
    }

    @Override
    public <T> FunctionArgumentType fromClass(Class<T> aClass) {
        if (arguments == null)
            init();

        return arguments.stream()
                .filter(type -> type.is(aClass))
                .reduce((joclArgumentType, joclArgumentType2) -> {
                    throw new RuntimeException("There are multiple JoclArgumentTypes matching " + aClass.getName());
                })
                .orElseGet(() -> {
                    JoclArgumentType joclArgumentType = baseFactory.fromClass(aClass);
                    return new PrimitiveWrapper(joclArgumentType);
                });
    }
}
