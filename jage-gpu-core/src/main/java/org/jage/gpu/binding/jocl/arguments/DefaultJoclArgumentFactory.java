package org.jage.gpu.binding.jocl.arguments;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jage.gpu.helpers.ThrowingFunction;
import org.reflections.Reflections;

public class DefaultJoclArgumentFactory implements JoclArgumentFactory {

    //it is not singleton, but there is no reason to create it again and again
    public final static DefaultJoclArgumentFactory INSTANCE = new DefaultJoclArgumentFactory();
    private List<JoclArgumentType> arguments;

    public void initJoclArguments() {
        Reflections reflections = new Reflections("");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(PrimitiveArgument.class);
        arguments = annotated.stream()
                .map(aClass -> {
                    if (!AbstractJoclArgumentType.class.isAssignableFrom(aClass))
                        throw new RuntimeException(aClass.getName() + " is annotated with " + PrimitiveArgument.class.getName());
                    return (Class<? extends JoclArgumentType>) aClass;
                })
                .map((ThrowingFunction<Class<? extends JoclArgumentType>, Constructor<? extends JoclArgumentType>>) aClass -> aClass.getDeclaredConstructor())
                .peek(constructor -> constructor.setAccessible(true))
                .map((ThrowingFunction<Constructor<? extends JoclArgumentType>, JoclArgumentType>) constructor -> constructor.newInstance())
                .collect(Collectors.toList());
    }

    @Override
    public JoclArgumentType fromName(String cTypeName) {
        if (arguments == null)
            initJoclArguments();

        return arguments.stream()
                .filter(type -> type.getNames().contains(cTypeName.trim()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Cannot parse type " + cTypeName));
    }

    @Override
    public <T> JoclArgumentType<T> fromClass(Class<T> aClass) {
        if (arguments == null)
            initJoclArguments();

        return arguments.stream()
                .filter(type -> type.is(aClass))
                .reduce((joclArgumentType, joclArgumentType2) -> {
                    throw new RuntimeException("There are multiple JoclArgumentTypes matching " + aClass.getName());
                }).orElseThrow(() -> new RuntimeException("Cannot parse type " + aClass.getName()));
    }

}
