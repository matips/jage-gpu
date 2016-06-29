package org.jage.gpu.binding.jocl.arguments;

public interface JoclArgumentFactory {
    JoclArgumentType fromName(String cTypeName);

    <T> JoclArgumentType fromClass(Class<T> aClass);
}
