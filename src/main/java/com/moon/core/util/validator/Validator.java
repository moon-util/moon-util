package com.moon.core.util.validator;

import java.util.List;

/**
 * 对象多条件验证器：
 *
 * @author benshaoye
 */
public final class Validator<T> extends BaseValidator<T, Validator<T>> {

    public Validator(T value) { this(value, null, SEPARATOR, false); }

    Validator(T value, List<String> messages, String separator, boolean immediate) {
        super(value, messages, separator, immediate);
    }

    public final static <T> Validator<T> of(T object) {
        return new Validator<>(object);
    }
}
