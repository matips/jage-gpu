package org.jage.gpu.binding.jocl.arguments.primitives;

import org.jage.gpu.binding.jocl.arguments.AbstractJoclArgumentType;

public abstract class JoclPrimitiveType<T> extends AbstractJoclArgumentType<T> {    //
    //    DOUBLE(double.class, "double"),
    //    FLOAT(float.class, "float"),
    //    CHAR(char.class, "char"),
    //    SHORT(short.class, "ushort"),

    protected JoclPrimitiveType(Class<T> javaClass, String... cNames) {
        super(javaClass, cNames);
    }

    @Override
    public boolean isArray() {
        return false;
    }
}
