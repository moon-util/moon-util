package com.moon.core.util.validator;

import java.util.List;

/**
 * 对象多条件验证器：
 *
 * @author benshaoye
 */
public final class Validator<T> extends BaseValidator<T, Validator<T>> {

    public Validator(T value) { this(value, false); }

    public Validator(T value, boolean nullable) { this(value, nullable, null, SEPARATOR, false); }

    Validator(T value, boolean nullable, List<String> messages, String separator, boolean immediate) {
        super(value, nullable, messages, separator, immediate);
    }

    public final static <T> Validator<T> of(T value) { return new Validator<>(value); }

    public final static <T> Validator<T> ofNullable(T value) { return new Validator<>(value, true); }
}
