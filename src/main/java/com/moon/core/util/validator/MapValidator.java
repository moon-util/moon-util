package com.moon.core.util.validator;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * @author benshaoye
 */
public final class MapValidator<M extends Map<K, V>, K, V>
    extends BaseValidator<M, MapValidator<M, K, V>>
    implements IKeyedValidator<M, K, V, MapValidator<M, K, V>> {

    public MapValidator(M value) {
        super(value, null, SEPARATOR, false);
    }

    MapValidator(M value, List<String> messages, String separator, boolean immediate) {
        super(value, messages, separator, immediate);
    }

    public final static <M extends Map<K, V>, K, V> MapValidator<M, K, V>
    of(M map) {
        return new MapValidator<>(map);
    }

    /*
     * -----------------------------------------------------
     * requires
     * -----------------------------------------------------
     */

    @Override
    public MapValidator<M, K, V> requireCountOf(BiPredicate<? super K, ? super V> tester, int count, String message) {
        return requireCountOf(this, tester, count, message);
    }

    @Override
    public MapValidator<M, K, V> requireAtLeastCountOf(
        BiPredicate<? super K, ? super V> tester, int count, String message) {
        return requireAtLeastCountOf(this, tester, count, message);
    }

    @Override
    public MapValidator<M, K, V> requireAtMostCountOf(
        BiPredicate<? super K, ? super V> tester, int count, String message) {
        return requireAtMostCountOf(this, tester, count, message);
    }
}
