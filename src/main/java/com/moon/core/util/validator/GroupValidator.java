package com.moon.core.util.validator;

import com.moon.core.util.MapUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

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

    public static <M extends Map<K, C>, K, C extends Collection<E>, E> GroupValidator<M, K, C, E> of(M map) {
        return new GroupValidator<>(map);
    }

    public static <M extends Map<K, C>, K, C extends Collection<E>, E> GroupValidator<M, K, C, E> ofNullable(M map) {
        return new GroupValidator<>(map, true);
    }

    /*
     * -----------------------------------------------------
     * requires
     * -----------------------------------------------------
     */

    @Override
    public GroupValidator<M, K, C, E> requireCountOf(
        int count, BiPredicate<? super K, ? super C> tester, String message
    ) { return requireCountOf(this, tester, count, message); }

    @Override
    public GroupValidator<M, K, C, E> requireAtLeastOf(
        int count, BiPredicate<? super K, ? super C> tester, String message
    ) { return requireAtLeastCountOf(this, tester, count, message); }

    @Override
    public GroupValidator<M, K, C, E> requireAtMostOf(
        int count, BiPredicate<? super K, ? super C> tester, String message
    ) { return requireAtMostCountOf(this, tester, count, message); }

    @Override
    public GroupValidator<M, K, C, E> forEach(BiConsumer<? super K, CollectValidator<C, E>> consumer) {
        return ifCondition(value -> {
            for (Map.Entry<K, C> item : value.entrySet()) {
                consumer.accept(item.getKey(),
                    new CollectValidator<>(item.getValue(), nullable, ensureMessages(), getSeparator(), isImmediate()));
            }
            return current();
        });
    }

    /**
     * 当验证通过时执行处理
     *
     * @param consumer 处理器
     *
     * @return 当前 Validator 对象
     */
    @Override
    public GroupValidator<M, K, C, E> ifValid(Consumer<? super M> consumer) {
        return super.ifValid(consumer);
    }

    /**
     * 当验证不通过时执行处理
     *
     * @param consumer 处理器
     *
     * @return 当前 Validator 对象
     */
    @Override
    public GroupValidator<M, K, C, E> ifInvalid(Consumer<? super M> consumer) {
        return super.ifInvalid(consumer);
    }

    /**
     * 直接添加一条错误消息
     *
     * @param message 消息内容
     *
     * @return 当前 Validator 实例
     */
    @Override
    public GroupValidator<M, K, C, E> addErrorMessage(String message) {
        return super.addErrorMessage(message);
    }

    /**
     * 可用于在后面条件验证前预先设置一部分默认值
     * <p>
     * 只能改变{@link #value}内部属性的值，不能重新设置 value
     *
     * @param consumer 预处理器
     *
     * @return 当前 Validator 实例
     */
    @Override
    public GroupValidator<M, K, C, E> preset(Consumer<? super M> consumer) {
        return super.preset(consumer);
    }

    /**
     * 设置是否立即终止，如果设置了即时终止，那么在设置之后第一个验证不通过会立即抛出异常
     * <p>
     * 否则在最后才抛出异常
     *
     * @param immediate 是否即时终止
     *
     * @return 当前 Validator 实例
     */
    @Override
    public GroupValidator<M, K, C, E> setImmediate(boolean immediate) {
        return super.setImmediate(immediate);
    }

    /**
     * 设置错误信息分隔符，不能为 null
     *
     * @param separator 错误消息分割符
     *
     * @return 当前 Validator 实例
     */
    @Override
    public GroupValidator<M, K, C, E> setSeparator(String separator) {
        return super.setSeparator(separator);
    }

    /**
     * 条件验证
     * <p>
     * 在前置条件匹配的情况下会执行 when 和 end 之间的验证或其他逻辑
     *
     * @param tester          进入条件验证的函数
     * @param scopedValidator 符合验证条件下的验证函数
     *
     * @return 当前 IValidator 对象
     */
    @Override
    public GroupValidator<M, K, C, E> when(
        Predicate<? super M> tester, Consumer<GroupValidator<M, K, C, E>> scopedValidator
    ) { return super.when(tester, scopedValidator); }

    /**
     * 要求符合条件
     *
     * @param tester  断言函数
     * @param message 错误消息
     *
     * @return 当前 Validator 对象
     */
    @Override
    public GroupValidator<M, K, C, E> require(Predicate<? super M> tester, String message) {
        return super.require(tester, message);
    }

    /**
     * 要求当存在指定映射时应该符合验证，使用指定错误信息
     *
     * @param key     存在指定键时验证对应项目
     * @param tester  验证函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    @Override
    public GroupValidator<M, K, C, E> requireKeyOf(
        K key, Predicate<? super C> tester, String message
    ) {
        M map = getValue();
        if (map.containsKey(key) && !tester.test(map.get(key))) {
            addErrorMessage(message);
        }
        return current();
    }

    /**
     * 要求在存在某个映射时应该符合验证
     *
     * @param key    存在指定键时验证对应项目
     * @param tester 验证函数
     *
     * @return 当前验证对象
     */
    @Override
    public GroupValidator<M, K, C, E> requireKeyOf(K key, Predicate<? super C> tester) {
        return requireKeyOf(key, tester, Value.NONE);
    }

    /**
     * 要求所有项都符合验证
     *
     * @param tester 验证函数
     *
     * @return 当前验证对象
     */
    @Override
    public GroupValidator<M, K, C, E> requireEvery(BiPredicate<? super K, ? super C> tester) {
        return requireEvery(tester, Value.NONE);
    }

    /**
     * 要求所有项都符合验证，使用指定错误信息
     *
     * @param tester  验证函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    @Override
    public GroupValidator<M, K, C, E> requireEvery(BiPredicate<? super K, ? super C> tester, String message) {
        return requireAtLeastOf(MapUtil.size(getValue()), tester, message);
    }

    /**
     * 要求至少一项符合验证
     *
     * @param tester 验证函数
     *
     * @return 当前验证对象
     */
    @Override
    public GroupValidator<M, K, C, E> requireAtLeast1(BiPredicate<? super K, ? super C> tester) {
        return requireAtLeast1(tester, Value.NONE);
    }

    /**
     * 要求至少一项符合验证，使用指定错误信息
     *
     * @param tester  验证函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    @Override
    public GroupValidator<M, K, C, E> requireAtLeast1(BiPredicate<? super K, ? super C> tester, String message) {
        return requireAtLeastOf(1, tester, message);
    }

    /**
     * 要求至少指定数目项符合验证
     *
     * @param count  符合验证条件的最小元素数量
     * @param tester 验证函数
     *
     * @return 当前验证对象
     */
    @Override
    public GroupValidator<M, K, C, E> requireAtLeastOf(int count, BiPredicate<? super K, ? super C> tester) {
        return requireAtLeastOf(count, tester, Value.NONE);
    }

    /**
     * 要求所有项都不符合验证
     *
     * @param tester 验证函数
     *
     * @return 当前验证对象
     */
    @Override
    public GroupValidator<M, K, C, E> requireNone(BiPredicate<? super K, ? super C> tester) {
        return requireNone(tester, Value.NONE);
    }

    /**
     * 要求所有项都不符合验证，使用指定错误信息
     *
     * @param tester  验证函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    @Override
    public GroupValidator<M, K, C, E> requireNone(BiPredicate<? super K, ? super C> tester, String message) {
        return requireAtMostOf(0, tester, message);
    }

    /**
     * 要求最多一项符合验证
     *
     * @param tester 验证函数
     *
     * @return 当前验证对象
     */
    @Override
    public GroupValidator<M, K, C, E> requireAtMost1(BiPredicate<? super K, ? super C> tester) {
        return requireAtMost1(tester, Value.NONE);
    }

    /**
     * 要求最多一项符合验证，使用指定错误信息
     *
     * @param tester  验证函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    @Override
    public GroupValidator<M, K, C, E> requireAtMost1(BiPredicate<? super K, ? super C> tester, String message) {
        return requireAtMostOf(1, tester, message);
    }

    /**
     * 要求最多指定数目项符合验证
     *
     * @param count  符合验证条件的最大元素数量
     * @param tester 验证函数
     *
     * @return 当前验证对象
     */
    @Override
    public GroupValidator<M, K, C, E> requireAtMostOf(int count, BiPredicate<? super K, ? super C> tester) {
        return requireAtMostOf(count, tester, Value.NONE);
    }

    /**
     * 要求包含唯一项符合验证
     *
     * @param tester 验证函数
     *
     * @return 当前验证对象
     */
    @Override
    public GroupValidator<M, K, C, E> requireOnly(BiPredicate<? super K, ? super C> tester) {
        return requireOnly(tester, Value.NONE);
    }

    /**
     * 要求包含唯一项符合验证，使用指定错误信息
     *
     * @param tester  验证函数
     * @param message 错误消息
     *
     * @return 当前验证对象
     */
    @Override
    public GroupValidator<M, K, C, E> requireOnly(BiPredicate<? super K, ? super C> tester, String message) {
        return requireCountOf(1, tester, message);
    }

    /**
     * 要求包含指定数目项符合验证
     *
     * @param count  符合验证条件的元素数量
     * @param tester 验证函数
     *
     * @return 当前验证对象
     */
    @Override
    public GroupValidator<M, K, C, E> requireCountOf(int count, BiPredicate<? super K, ? super C> tester) {
        return requireCountOf(count, tester, Value.NONE);
    }

    /**
     * 要求符合指定验证规则
     *
     * @param tester 验证函数
     *
     * @return 当前 IValidator 对象
     */
    @Override
    public GroupValidator<M, K, C, E> require(Predicate<? super M> tester) {
        return require(tester, Value.NONE);
    }
}
