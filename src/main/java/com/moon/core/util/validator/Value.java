package com.moon.core.util.validator;

import com.moon.core.enums.Const;
import com.moon.core.util.interfaces.ValueSupplier;

import java.util.Objects;

/**
 * @author benshaoye
 */
abstract class Value<T> implements ValueSupplier<T> {

    final static String NONE = Const.EMPTY;

    final T value;

    Value(T value) {this.value = Objects.requireNonNull(value);}

    @Override
    public final T getValue() {
        return value;
    }
}
