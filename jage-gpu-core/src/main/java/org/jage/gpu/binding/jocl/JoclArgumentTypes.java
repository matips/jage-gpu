package org.jage.gpu.binding.jocl;

import java.util.Arrays;
import java.util.List;

import org.jage.gpu.binding.ArgumentType;

public enum JoclArgumentTypes implements ArgumentType {
    DOUBLE_ARRAY(double[].class, "double*"),
    FLOAT_ARRAY(float[].class, "float*"),
    CHAR_ARRAY(char[].class, "char*"),
    INT_ARRAY(int[].class, "uint*"),

    DOUBLE(double.class, "double"),
    FLOAT(float.class, "float"),
    CHAR(char.class, "char"),
    SHORT(short.class, "ushort"),
    INT(int.class, "uint");


    private final List<String> names;
    private final Class javaClass;

    JoclArgumentTypes(Class javaClass, String... cNames) {
        this.javaClass = javaClass;
        this.names = Arrays.asList(cNames);
    }

    @Override
    public String toString() {
        return names.get(0);
    }

    public static JoclArgumentTypes fromName(String cTypeName) {
        return Arrays.stream(values())
                .filter(type -> type.names.contains(cTypeName))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Cannot parse type " + cTypeName));
    }

    @Override
    public boolean isArray() {
        return javaClass.isArray();
    }

    @Override
    public boolean isPointer() {
        return javaClass.isArray(); //in c pointers and arrays are identical
    }

    @Override
    public boolean is(Class javaType){
        return this.javaClass.equals(javaType);
    }

    @Override
    public ArgumentType toArray() {
        if (isArray()) {
            throw new RuntimeException("OpenCL does not supports pointers of pointers.");
        } else {
            return JoclArgumentTypes.fromName(toString().concat("*"));
        }
    }
}
