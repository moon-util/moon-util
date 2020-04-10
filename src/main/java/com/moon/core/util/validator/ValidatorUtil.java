package com.moon.core.util.validator;

import java.util.Collection;
import java.util.Map;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class ValidatorUtil {
    private ValidatorUtil() { noInstanceError(); }

    public static <T> Validator<T> of(T value) {return Validator.of(value);}

    public static <C extends Collection<E>, E> CollectValidator<C, E> ofCollect(C collect) {
        return CollectValidator.of(collect);
    }

    public static <M extends Map<K, V>, K, V> MapValidator<M, K, V> ofMap(M collect) {
        return MapValidator.of(collect);
    }

    public static IDCard18Validator ofIDCard18(String value) {return IDCard18Validator.of(value);}
}
