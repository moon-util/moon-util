package com.moon.core.util.validator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * @author benshaoye
 */
public final class GroupValidator<M extends Map<K, C>, K, C extends Collection<E>, E>
    extends BaseValidator<M, GroupValidator<M, K, C, E>>
    implements IGroupValidator<M, C, K, E, GroupValidator<M, K, C, E>> {

    public GroupValidator(M value) { this(value, false); }

    public GroupValidator(M value, boolean nullable) { super(value, nullable, null, SEPARATOR, false); }

    GroupValidator(M value, boolean nullable, List<String> messages, String separator, boolean immediate) {
        super(value, nullable, messages, separator, immediate);
    }

    public final static <M extends Map<K, C>, K, C extends Collection<E>, E>
    GroupValidator<M, K, C, E> of(M map) { return new GroupValidator<>(map); }

    public final static <M extends Map<K, C>, K, C extends Collection<E>, E>
    GroupValidator<M, K, C, E> ofNullable(M map) { return new GroupValidator<>(map, true); }

    /*
     * -----------------------------------------------------
     * requires
     * -----------------------------------------------------
     */

    @Override
    public GroupValidator<M, K, C, E> requireCountOf(int count, BiPredicate<? super K, ? super C> tester, String message) {
        return requireCountOf(this, tester, count, message);
    }

    @Override
    public GroupValidator<M, K, C, E> requireAtLeastOf(
        int count, BiPredicate<? super K, ? super C> tester, String message) {
        return requireAtLeastCountOf(this, tester, count, message);
    }

    @Override
    public GroupValidator<M, K, C, E> requireAtMostOf(
        int count, BiPredicate<? super K, ? super C> tester, String message) {
        return requireAtMostCountOf(this, tester, count, message);
    }

    @Override
    public GroupValidator<M, K, C, E> requireForEach(BiConsumer<? super K, CollectValidator<C, E>> consumer) {
        return ifWhen(value -> {
            for (Map.Entry<K, C> item : value.entrySet()) {
                consumer.accept(item.getKey(), new CollectValidator(item.getValue(), nullable,
                    ensureMessages(), getSeparator(), isImmediate()));
            }
        });
    }
}
