package com.moon.accessor.function;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface BooleanTableApplier<P1,P2, R> {

    R apply(P1 param1,P2 param2, boolean value);
}
