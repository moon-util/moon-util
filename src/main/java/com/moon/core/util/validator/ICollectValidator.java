package com.moon.core.util.validator;

import com.moon.core.util.CollectUtil;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author benshaoye
 */
interface ICollectValidator<C extends Collection<E>, E, IMPL> extends IValidator<C, IMPL> {

    /**
     * 要求至少指定数目项符合验证，使用指定错误信息
     *
     * @param tester
     * @param count
     * @param message
     *
     * @return
     */
    IMPL requireAtLeastOf(int count, Predicate<? super E> tester, String message);

    /**
     * 要求最多指定数目项符合验证，使用指定错误信息
     *
     * @param tester
     * @param count
     * @param message
     *
     * @return
     */
    IMPL requireAtMostOf(int count, Predicate<? super E> tester, String message);

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
     *
     * @return
     */
    default IMPL requireEvery(Predicate<? super E> tester) { return requireEvery(tester, "requireEvery"); }

    /**
     * 要求所有项都符合验证，使用指定错误信息
     *
     * @param tester
     * @param message
     *
     * @return
     */
    default IMPL requireEvery(Predicate<? super E> tester, String message) {
        return requireAtLeastOf(CollectUtil.size(getValue()), tester, message);
    }

    /**
     * 要求至少一项符合验证
     *
     * @param tester
     *
     * @return
     */
    default IMPL requireAtLeast1(Predicate<? super E> tester) {
        return requireAtLeast1(tester, "requireAtLeast1");
    }

    /**
     * 要求至少一项符合验证，使用指定错误信息
     *
     * @param tester
     * @param message
     *
     * @return
     */
    default IMPL requireAtLeast1(Predicate<? super E> tester, String message) {
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
    default IMPL requireAtLeastOf(int count, Predicate<? super E> tester) {
        return requireAtLeastOf(count, tester, "requireAtLeastCountOf");
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
    default IMPL requireNone(Predicate<? super E> tester) { return requireNone(tester, "requireNone"); }

    /**
     * 要求所有项都不符合验证，使用指定错误信息
     *
     * @param tester
     * @param message
     *
     * @return
     */
    default IMPL requireNone(Predicate<? super E> tester, String message) {
        return requireAtMostOf(0, tester, message);
    }

    /**
     * 要求最多一项符合验证
     *
     * @param tester
     *
     * @return
     */
    default IMPL requireAtMost1(Predicate<? super E> tester) { return requireAtMost1(tester, "requireAtMost1"); }

    /**
     * 要求最多一项符合验证，使用指定错误信息
     *
     * @param tester
     * @param message
     *
     * @return
     */
    default IMPL requireAtMost1(Predicate<? super E> tester, String message) {
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
    default IMPL requireAtMostOf(int count, Predicate<? super E> tester) {
        return requireAtMostOf(count, tester, "requireAtMostCountOf");
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
    default IMPL requireOnly(Predicate<? super E> tester) { return requireOnly(tester, "requireOnly"); }

    /**
     * 要求包含唯一项符合验证，使用指定错误信息
     *
     * @param tester
     * @param message
     *
     * @return
     */
    default IMPL requireOnly(Predicate<? super E> tester, String message) { return requireCountOf(1, tester, message); }

    /**
     * 要求包含指定数目项符合验证
     *
     * @param tester
     * @param count
     *
     * @return
     */
    default IMPL requireCountOf(int count, Predicate<? super E> tester) {
        return requireCountOf(count, tester, "requireCountOf");
    }

    /**
     * 要求包含指定数目项符合验证，使用指定错误信息
     *
     * @param tester
     * @param count
     * @param message
     *
     * @return
     */
    IMPL requireCountOf(int count, Predicate<? super E> tester, String message);
}
