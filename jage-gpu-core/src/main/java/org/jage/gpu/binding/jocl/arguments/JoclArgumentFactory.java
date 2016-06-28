package org.jage.gpu.binding.jocl.arguments;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jage.gpu.helpers.ThrowingFunction;
import org.reflections.Reflections;

public class JoclArgumentFactory {

    private static List<JoclArgumentType> arguments;

    public static void initJoclArguments() {
        Reflections reflections = new Reflections("");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(GlobalArgument.class);
        arguments = annotated.stream()
                .map(aClass -> {
                    if (!JoclArgumentType.class.isAssignableFrom(aClass))
                        throw new RuntimeException(aClass.getName() + " is annotated with " + GlobalArgument.class.getName());
                    return (Class<? extends JoclArgumentType>) aClass;
                })
                .map((ThrowingFunction<Class<? extends JoclArgumentType>, Constructor<? extends JoclArgumentType>>) aClass -> aClass.getDeclaredConstructor())
                .peek(constructor -> constructor.setAccessible(true))
                .map((ThrowingFunction<Constructor<? extends JoclArgumentType>, JoclArgumentType>) constructor -> constructor.newInstance())
                .collect(Collectors.toList());
    }

    public static JoclArgumentType fromName(String cTypeName) {
        if (arguments == null)
            initJoclArguments();

        return arguments.stream()
                .filter(type -> type.names.contains(cTypeName.trim()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Cannot parse type " + cTypeName));
    }

    public static<T> JoclArgumentType<T> fromClass(Class<T> aClass) {
        if (arguments == null)
            initJoclArguments();

        return arguments.stream()
                .filter(type -> type.is(aClass))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Cannot parse type " + aClass.getName()));
    }

}
