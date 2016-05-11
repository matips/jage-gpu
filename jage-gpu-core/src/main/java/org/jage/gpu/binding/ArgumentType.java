package org.jage.gpu.binding;

public interface ArgumentType {
    boolean isArray();

    boolean isPointer();

    ArgumentType toArray();

    /**
     * @param javaType
     * @return true if represented GPU type match javaType. For example jocl "double" match {@link Double}
     */
    boolean is(Class javaType);
}
