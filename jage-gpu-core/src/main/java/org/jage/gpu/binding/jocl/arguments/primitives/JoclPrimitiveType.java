package org.jage.gpu.binding.jocl.arguments.primitives;

import com.google.common.collect.Sets;
import org.jage.gpu.binding.ArgumentAddressQualifier;
import org.jage.gpu.binding.jocl.arguments.AbstractJoclArgumentType;

import java.util.Set;

public abstract class JoclPrimitiveType<T> extends AbstractJoclArgumentType<T> {    //
    //    DOUBLE(double.class, "double"),
    //    FLOAT(float.class, "float"),
    //    CHAR(char.class, "char"),
    //    SHORT(short.class, "ushort"),

    protected JoclPrimitiveType(Class<T> javaClass, String... cNames) {
        super(javaClass, cNames);
    }

    @Override
    public Set<ArgumentAddressQualifier> validAddressSpaces() {
        return Sets.immutableEnumSet(ArgumentAddressQualifier.PRIVATE);
    }

    @Override
    public boolean isArray() {
        return false;
    }
}
