package org.jage.gpu.binding;

import java.util.Arrays;
import java.util.List;

public enum ArgumentType {
    DOUBLE_ARRAY("double*"),FLOAT_ARRAY("float*"),CHAR_ARRAY("char*"),INT_ARRAY("uint*"),SHORT("ushort"),IMAGE_2D("image2d_t"), INT("uint");

    private final List<String> names;

    ArgumentType(String... cNames) {
        this.names = Arrays.asList(cNames);
    }

    public static ArgumentType fromName(String cTypeName) {
        return Arrays.stream(values())
                .filter(type -> type.names.contains(cTypeName))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Cannot parse type " + cTypeName));
    }
}
