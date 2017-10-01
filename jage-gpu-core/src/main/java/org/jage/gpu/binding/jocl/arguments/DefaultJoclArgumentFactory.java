package org.jage.gpu.binding.jocl.arguments;

import org.jage.gpu.binding.ArgumentAddressQualifier;
import org.jage.gpu.binding.ArgumentTypeQualifier;
import org.jage.gpu.helpers.ThrowingFunction;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultJoclArgumentFactory implements JoclArgumentFactory {

    //it is not singleton, but there is no reason to create it again and again
    public final static DefaultJoclArgumentFactory INSTANCE = new DefaultJoclArgumentFactory();
    private List<JoclArgumentType> arguments;

    public void initJoclArguments() {
        Reflections reflections = new Reflections("org.jage.gpu");
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
    public JoclArgumentType from(String cTypeName, ArgumentAddressQualifier argumentAddressQualifier, ArgumentTypeQualifier argumentTypeQualifier) {
        if (arguments == null)
            initJoclArguments();

        ArgumentAddressQualifier searchArgumentAddressQualifier;
        if (argumentTypeQualifier == ArgumentTypeQualifier.CONST) { //it is hack, kernels has limit of constant variables
            searchArgumentAddressQualifier = ArgumentAddressQualifier.CONSTANT;
        } else {
            searchArgumentAddressQualifier = argumentAddressQualifier;
        }

        return arguments.stream()
                .filter(type -> type.getNames().contains(cTypeName.trim()))
                .filter(type -> type.validAddressSpaces().contains(searchArgumentAddressQualifier))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Cannot parse type " + cTypeName));
    }

    @Override
    public <T extends JoclArgumentType> T fromClass(Class<T> aClass) {
        if (arguments == null)
            initJoclArguments();

        return arguments.stream()
                .filter(type -> aClass.isAssignableFrom(type.getClass()))
                .reduce((joclArgumentType, joclArgumentType2) -> {
                    throw new RuntimeException("There are multiple JoclArgumentTypes matching " + aClass.getName());
                })
                .map(type -> (T) type)
                .orElseThrow(() -> new RuntimeException("Cannot parse type " + aClass.getName()));
    }

}
