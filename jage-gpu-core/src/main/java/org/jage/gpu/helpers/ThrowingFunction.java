package org.jage.gpu.helpers;

import java.util.function.Function;

public interface ThrowingFunction<T, R> extends Function<T, R> {
    @Override
    default R apply(T t) {
        try {
            return throwingApply(t);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    R throwingApply(T t) throws Exception;

}
