package org.jage.gpu.binding.jocl.arguments.arrays;

import org.jage.gpu.binding.ArgumentType;
import org.jage.gpu.binding.jocl.arguments.AbstractJoclArgumentType;

public abstract class JoclArrayType<T> extends AbstractJoclArgumentType<T> {
    protected JoclArrayType(Class<T> javaClass, String... cNames) {
        super(javaClass, cNames);
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public ArgumentType toArray() {
        throw new RuntimeException("OpenCL does not supports pointers of pointers.");
    }


}
