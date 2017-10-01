package org.jage.gpu.binding.jocl.arguments;

import org.jage.gpu.binding.ArgumentAddressQualifier;
import org.jage.gpu.binding.ArgumentTypeQualifier;

public interface JoclArgumentFactory {
    JoclArgumentType from(String cTypeName, ArgumentAddressQualifier argumentAddressQualifier, ArgumentTypeQualifier argumentTypeQualifier);

    <T extends JoclArgumentType> T fromClass(Class<T> aClass);
}
