package org.jage.gpu.binding.jocl.kernelAsFunction.arguments;

import org.jage.gpu.binding.ArgumentAddressQualifier;
import org.jage.gpu.binding.ArgumentTypeQualifier;
import org.jage.gpu.binding.jocl.arguments.DefaultJoclArgumentFactory;
import org.jage.gpu.binding.jocl.arguments.JoclArgumentFactory;
import org.jage.gpu.binding.jocl.arguments.JoclArgumentType;
import org.jage.gpu.helpers.ThrowingFunction;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                    throw new RuntimeException(aClass.getName() + " is annotated with " + GlobalArgument.class.getName() + " but is not FunctionArtumentType");
                })
                .map((ThrowingFunction<Class<? extends FunctionArgumentType>, Constructor<? extends FunctionArgumentType>>) aClass -> aClass
                        .getDeclaredConstructor())
                .peek(constructor -> constructor.setAccessible(true))
                .map((ThrowingFunction<Constructor<? extends FunctionArgumentType>, FunctionArgumentType>) constructor -> constructor.newInstance())
                .collect(Collectors.toList());
    }

    public List<FunctionArgumentType> allTypes() {
        if (arguments == null)
            init();
        return arguments;
    }

    @Override
    public JoclArgumentType from(String cTypeName, ArgumentAddressQualifier argumentAddressQualifier, ArgumentTypeQualifier argumentTypeQualifier) {
        if (arguments == null)
            init();

        return arguments.stream()
                .filter(type -> type.getNames().contains(cTypeName.trim()))
                .map(a -> (JoclArgumentType) a)
                .findAny()
                .orElseGet(() -> baseFactory.from(cTypeName, argumentAddressQualifier, argumentTypeQualifier));
    }

    @Override
    public <T extends JoclArgumentType> T fromClass(Class<T> aClass) {
        if (arguments == null)
            init();

        return arguments.stream()
                .filter(type -> aClass.isAssignableFrom(type.getClass()))
                .reduce((joclArgumentType, joclArgumentType2) -> {
                    throw new RuntimeException("There are multiple JoclArgumentTypes matching " + aClass.getName());
                })
                .map(type -> (T) type)
                .orElseThrow(() -> new RuntimeException("Cannot parse type " + aClass.getName()));
    }
}
