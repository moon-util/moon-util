package com.moon.core.util.validator;

import com.moon.core.util.CollectUtil;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author benshaoye
 */
interface ICollectValidator<C extends Collection<E>, E, IMPL>
    extends IValidator<C, IMPL> {

    /**
     * 要求至少指定数目项符合验证，使用指定错误信息
     *
     * @param tester
     * @param count
     * @param message
     * @return
     */
    IMPL requireAtLeastCountOf(Predicate<? super E> tester, int count, String message);

    /**
     * 要求最多指定数目项符合验证，使用指定错误信息
     *
     * @param tester
     * @param count
     * @param message
     * @return
     */
    IMPL requireAtMostCountOf(Predicate<? super E> tester, int count, String message);

    /*
     * -----------------------------------------------------
     * implemented
     * -----------------------------------------------------
     */

    /*
     * -----------------------------------------------------
     * at least
     * -----------------------------------------------------
     */

    /**
     * 要求所有项都符合验证
     *
     * @param tester
     * @return
     */
    default IMPL requireEvery(Predicate<? super E> tester) {
        return requireEvery(tester, "requireEvery");
    }

    /**
     * 要求所有项都符合验证，使用指定错误信息
     *
     * @param tester
     * @param message
     * @return
     */
    default IMPL requireEvery(Predicate<? super E> tester, String message) {
        return requireAtLeastCountOf(tester, CollectUtil.size(getValue()), message);
    }

    /**
     * 要求至少一项符合验证
     *
     * @param tester
     * @return
     */
    default IMPL requireAtLeastOne(Predicate<? super E> tester) {
        return requireAtLeastOne(tester, "requireAtLeastOne");
    }

    /**
     * 要求至少一项符合验证，使用指定错误信息
     *
     * @param tester
     * @param message
     * @return
     */
    default IMPL requireAtLeastOne(Predicate<? super E> tester, String message) {
        return requireAtLeastCountOf(tester, 1, message);
    }

    /**
     * 要求至少指定数目项符合验证
     *
     * @param tester
     * @param count
     * @return
     */
    default IMPL requireAtLeastCountOf(Predicate<? super E> tester, int count) {
        return requireAtLeastCountOf(tester, count, "requireAtLeastCountOf");
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
     * @return
     */
    default IMPL requireNone(Predicate<? super E> tester) {
        return requireNone(tester, "requireNone");
    }

    /**
     * 要求所有项都不符合验证，使用指定错误信息
     *
     * @param tester
     * @param message
     * @return
     */
    default IMPL requireNone(Predicate<? super E> tester, String message) {
        return requireAtMostCountOf(tester, 0, message);
    }

    /**
     * 要求最多一项符合验证
     *
     * @param tester
     * @return
     */
    default IMPL requireAtMostOne(Predicate<? super E> tester) {
        return requireAtMostOne(tester, "requireAtMostOne");
    }

    /**
     * 要求最多一项符合验证，使用指定错误信息
     *
     * @param tester
     * @param message
     * @return
     */
    default IMPL requireAtMostOne(Predicate<? super E> tester, String message) {
        return requireAtMostCountOf(tester, 1, message);
    }

    /**
     * 要求最多指定数目项符合验证
     *
     * @param tester
     * @param count
     * @return
     */
    default IMPL requireAtMostCountOf(Predicate<? super E> tester, int count) {
        return requireAtMostCountOf(tester, count, "requireAtMostCountOf");
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
     * @return
     */
    default IMPL requireOnly(Predicate<? super E> tester) {
        return requireOnly(tester, "requireOnly");
    }

    /**
     * 要求包含唯一项符合验证，使用指定错误信息
     *
     * @param tester
     * @param message
     * @return
     */
    default IMPL requireOnly(Predicate<? super E> tester, String message) {
        return requireCountOf(tester, 1, message);
    }

    /**
     * 要求包含指定数目项符合验证
     *
     * @param tester
     * @param count
     * @return
     */
    default IMPL requireCountOf(Predicate<? super E> tester, int count) {
        return requireCountOf(tester, count, "requireCountOf");
    }

    /**
     * 要求包含指定数目项符合验证，使用指定错误信息
     *
     * @param tester
     * @param count
     * @param message
     * @return
     */
    IMPL requireCountOf(Predicate<? super E> tester, int count, String message);
}
