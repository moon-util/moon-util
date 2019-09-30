package com.moon.core.util.validator;

import com.moon.core.util.FilterUtil;
import com.moon.core.util.GroupUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 集合验证器：
 * 首先请参考{@link Validator}文档。CollectValidator 继承了 Validator；具备基本的对象验证功能。
 *
 * @author benshaoye
 */
public final class CollectValidator<C extends Collection<E>, E> extends BaseValidator<C, CollectValidator<C, E>>
    implements ICollectValidator<C, E, CollectValidator<C, E>> {

    public CollectValidator(C value) { this(value, false); }

    public CollectValidator(C value, boolean nullable) { this(value, nullable, null, SEPARATOR, false); }

    CollectValidator(C value, boolean nullable, List<String> messages, String separator, boolean immediate) {
        super(value, nullable, messages, separator, immediate);
    }

    public final static <C extends Collection<E>, E> CollectValidator<C, E> of(C collect) {
        return new CollectValidator<>(collect);
    }

    public final static <C extends Collection<E>, E> CollectValidator<C, E> ofNullable(C collect) {
        return new CollectValidator<>(collect, true);
    }


    /*
     * -----------------------------------------------------
     * requires
     * -----------------------------------------------------
     */

    @Override
    public CollectValidator<C, E> requireAtLeastCountOf(Predicate<? super E> tester, int count, String message) {
        return ifCondition(value -> {
            int amount = 0;
            for (E item : value) {
                if (tester.test(item) && (++amount >= count)) {
                    return this;
                }
            }
            return amount < count ? createMsgAtLeast(message, count) : this;
        });
    }

    @Override
    public CollectValidator<C, E> requireAtMostCountOf(Predicate<? super E> tester, int count, String message) {
        return ifCondition(value -> {
            int amount = 0;
            for (E item : value) {
                if (tester.test(item) && (++amount > count)) {
                    return createMsgAtMost(message, count);
                }
            }
            return amount > count ? createMsgAtMost(message, count) : this;
        });
    }

    @Override
    public CollectValidator<C, E> requireCountOf(Predicate<? super E> tester, int count, String message) {
        return ifCondition(value -> {
            int amount = 0;
            for (E item : value) {
                if (tester.test(item) && (++amount > count)) {
                    return createMsgCountOf(message, count);
                }
            }
            return amount < count ? createMsgCountOf(message, count) : this;
        });
    }

    /*
     * -----------------------------------------------------
     * group by
     * -----------------------------------------------------
     */

    public final <O> GroupValidator<Map<O, Collection<E>>, O, Collection<E>, E> groupBy(Function<? super E, O> grouper) {
        return new GroupValidator<>(GroupUtil.groupBy(value, grouper), nullable, ensureMessages(), getSeparator(),
            isImmediate());
    }

    /*
     * -----------------------------------------------------
     * filter
     * -----------------------------------------------------
     */

    public final CollectValidator<List<E>, E> filter(Predicate<? super E> filter) {
        return new CollectValidator<>(FilterUtil.filter(value, filter, new ArrayList<>()), nullable, ensureMessages(),
            getSeparator(), isImmediate());
    }
}
