package com.moon.core.util.validator;

import com.moon.core.util.MapUtil;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * @author benshaoye
 */
interface IKeyedValidator<M extends Map<K, V>, K, V, IMPL> extends IValidator<M, IMPL> {

    /**
     * 要求包含指定数目项符合验证，使用指定错误信息
     *
     * @param tester
     * @param count
     * @param message
     *
     * @return
     */
    IMPL requireCountOf(int count, BiPredicate<? super K, ? super V> tester, String message);

    /**
     * 要求至少指定数目项符合验证，使用指定错误信息
     *
     * @param tester
     * @param count
     * @param message
     *
     * @return
     */
    IMPL requireAtLeastOf(int count, BiPredicate<? super K, ? super V> tester, String message);

    /**
     * 要求最多指定数目项符合验证，使用指定错误信息
     *
     * @param tester
     * @param count
     * @param message
     *
     * @return
     */
    IMPL requireAtMostOf(int count, BiPredicate<? super K, ? super V> tester, String message);

    /*
     * -----------------------------------------------------
     * implemented
     * -----------------------------------------------------
     */

    /**
     * 要求当存在指定映射时应该符合验证，使用指定错误信息
     *
     * @param key
     * @param tester
     * @param message
     *
     * @return
     */
    default IMPL requireIfPresent(K key, Predicate<? super V> tester, String message) {
        return ifWhen(m -> {
            if (m.containsKey(key) && tester.test(m.get(key))) {
                addErrorMessage(message);
            }
        });
    }

    /**
     * 要求在存在某个映射时应该符合验证
     *
     * @param key
     * @param tester
     *
     * @return
     */
    default IMPL requireIfPresent(K key, Predicate<? super V> tester) {
        return requireIfPresent(key, tester, Value.NONE);
    }

    /*
     * -----------------------------------------------------
     * at least
     * -----------------------------------------------------
     */

    /**
     * 要求所有项都符合验证
     *
     * @param tester
     *
     * @return
     */
    default IMPL requireEvery(BiPredicate<? super K, ? super V> tester) { return requireEvery(tester, Value.NONE); }

    /**
     * 要求所有项都符合验证，使用指定错误信息
     *
     * @param tester
     * @param message
     *
     * @return
     */
    default IMPL requireEvery(BiPredicate<? super K, ? super V> tester, String message) {
        return requireAtLeastOf(MapUtil.size(getValue()), tester, message);
    }

    /**
     * 要求至少一项符合验证
     *
     * @param tester
     *
     * @return
     */
    default IMPL requireAtLeast1(BiPredicate<? super K, ? super V> tester) {
        return requireAtLeast1(tester, Value.NONE);
    }

    /**
     * 要求至少一项符合验证，使用指定错误信息
     *
     * @param tester
     * @param message
     *
     * @return
     */
    default IMPL requireAtLeast1(BiPredicate<? super K, ? super V> tester, String message) {
        return requireAtLeastOf(1, tester, message);
    }

    /**
     * 要求至少指定数目项符合验证
     *
     * @param tester
     * @param count
     *
     * @return
     */
    default IMPL requireAtLeastOf(int count, BiPredicate<? super K, ? super V> tester) {
        return requireAtLeastOf(count, tester, Value.NONE);
    }

    /*
     * -----------------------------------------------------
     * at most
     * -----------------------------------------------------
     */

    /**
     * 要求所有项都不符合验证
     *
     * @param tester
     *
     * @return
     */
    default IMPL requireNone(BiPredicate<? super K, ? super V> tester) { return requireNone(tester, Value.NONE); }

    /**
     * 要求所有项都不符合验证，使用指定错误信息
     *
     * @param tester
     * @param message
     *
     * @return
     */
    default IMPL requireNone(BiPredicate<? super K, ? super V> tester, String message) {
        return requireAtMostOf(0, tester, message);
    }

    /**
     * 要求最多一项符合验证
     *
     * @param tester
     *
     * @return
     */
    default IMPL requireAtMost1(BiPredicate<? super K, ? super V> tester) {
        return requireAtMost1(tester, Value.NONE);
    }

    /**
     * 要求最多一项符合验证，使用指定错误信息
     *
     * @param tester
     * @param message
     *
     * @return
     */
    default IMPL requireAtMost1(BiPredicate<? super K, ? super V> tester, String message) {
        return requireAtMostOf(1, tester, message);
    }

    /**
     * 要求最多指定数目项符合验证
     *
     * @param tester
     * @param count
     *
     * @return
     */
    default IMPL requireAtMostOf(int count, BiPredicate<? super K, ? super V> tester) {
        return requireAtMostOf(count, tester, Value.NONE);
    }

    /*
     * -----------------------------------------------------
     * count of
     * -----------------------------------------------------
     */

    /**
     * 要求包含唯一项符合验证
     *
     * @param tester
     *
     * @return
     */
    default IMPL requireOnly(BiPredicate<? super K, ? super V> tester) { return requireOnly(tester, Value.NONE); }

    /**
     * 要求包含唯一项符合验证，使用指定错误信息
     *
     * @param tester
     * @param message
     *
     * @return
     */
    default IMPL requireOnly(BiPredicate<? super K, ? super V> tester, String message) {
        return requireCountOf(1, tester, message);
    }

    /**
     * 要求包含指定数目项符合验证
     *
     * @param tester
     * @param count
     *
     * @return
     */
    default IMPL requireCountOf(int count, BiPredicate<? super K, ? super V> tester) {
        return requireCountOf(count, tester, Value.NONE);
    }
}
