package org.jage.gpu.binding.jocl.kernelAsFunction.arguments;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
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
        Reflections reflections = new Reflections("org");
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
    public JoclArgumentType fromName(String cTypeName) {
        if (arguments == null)
            init();

        return arguments.stream()
                .filter(type -> type.getNames().contains(cTypeName.trim()))
                .map(a -> (JoclArgumentType) a)
                .findAny()
                .orElseGet(() -> baseFactory.fromName(cTypeName));
    }

    @Override
    public <T> JoclArgumentType fromClass(Class<T> aClass) {
        if (arguments == null)
            init();

        return arguments.stream()
                .filter(type -> type.is(aClass))
                .reduce((joclArgumentType, joclArgumentType2) -> {
                    throw new RuntimeException("There are multiple JoclArgumentTypes matching " + aClass.getName());
                })
                .map(a -> (JoclArgumentType) a)
                .orElseGet(() -> baseFactory.fromClass(aClass));
    }
}
