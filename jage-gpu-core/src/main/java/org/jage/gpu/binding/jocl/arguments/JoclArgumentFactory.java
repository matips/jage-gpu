package org.jage.gpu.binding.jocl.arguments;

import org.jage.gpu.binding.ArgumentAddressQualifier;

public interface JoclArgumentFactory {
    JoclArgumentType from(String cTypeName, ArgumentAddressQualifier argumentAddressQualifier);

    <T extends JoclArgumentType> T fromClass(Class<T> aClass);
}
