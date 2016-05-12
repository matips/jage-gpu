package org.jage.gpu.binding.jocl.arguments;

import java.util.Arrays;

public class JoclArgumentFactory {

    public static JoclPrimitiveArgumentTypes fromName(String cTypeName) {
        return Arrays.stream(JoclPrimitiveArgumentTypes.values())
                .filter(type -> type.names.contains(cTypeName))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Cannot parse type " + cTypeName));
    }

}
