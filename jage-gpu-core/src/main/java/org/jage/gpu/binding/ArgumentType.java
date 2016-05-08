package org.jage.gpu.binding;

import java.util.Arrays;
import java.util.List;

public enum ArgumentType {
    DOUBLE_ARRAY(true, "double*"),
    FLOAT_ARRAY(true, "float*"),
    CHAR_ARRAY(true, "char*"),
    INT_ARRAY(true, "uint*"),

    DOUBLE(false, "double"),
    FLOAT(false, "float"),
    CHAR(false, "char"),
    SHORT(false, "ushort"),
    INT(false, "uint"),

    IMAGE_2D(false, "image2d_t");

    private final List<String> names;
    private final boolean array;

    ArgumentType(boolean array, String... cNames) {
        this.array = array;
        this.names = Arrays.asList(cNames);
    }

    @Override
    public String toString() {
        return names.get(0);
    }

    public static ArgumentType fromName(String cTypeName) {
        return Arrays.stream(values())
                .filter(type -> type.names.contains(cTypeName))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Cannot parse type " + cTypeName));
    }

    public boolean isArray() {
        return array;
    }
    public boolean isPointer() {
        return array; //in c pointers and arrays are identical
    }

}
