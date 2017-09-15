package org.jage.gpu.binding.jocl.arguments;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractJoclArgumentType<T> implements JoclArgumentType<T> {
    final List<String> names;
    final Class javaClass;

    protected AbstractJoclArgumentType(Class<T> javaClass, String... cNames) {
        if (isArray() ^ javaClass.isArray())
            throw new IllegalArgumentException(javaClass.toString() + (isArray() ? " is not an array" : "is array "));
        this.javaClass = javaClass;
        this.names = Arrays.asList(cNames);
    }

    @Override
    public String toString() {
        return getCName();
    }

    @Override
    public boolean is(Class javaType) {
        return this.javaClass.equals(javaType);
    }

    @Override
    public boolean isPointer() {
        return this.isArray(); //in c pointers and arrays are identical
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AbstractJoclArgumentType<?> that = (AbstractJoclArgumentType<?>) o;

        if (!names.equals(that.names) || isArray() ^ that.isArray() || isPointer() ^ that.isPointer())
            return false;
        return javaClass.equals(that.javaClass);

    }

    @Override
    public int hashCode() {
        int result = names.hashCode();
        result = 31 * result + javaClass.hashCode();
        return result;
    }

    @Override
    public List<String> getNames() {
        return names;
    }

    @Override
    public String getCName() {
        return names.get(0);
    }
}
