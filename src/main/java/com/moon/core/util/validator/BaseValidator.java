package com.moon.core.util.validator;

import com.moon.core.lang.JoinerUtil;
import com.moon.core.lang.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.*;

/**
 * @author benshaoye
 */
abstract class BaseValidator<T, IMPL extends BaseValidator<T, IMPL>> extends Value<T>
    implements Cloneable, Serializable, IValidator<T, IMPL>, Supplier<T> {

    static final long serialVersionUID = 1L;

    static final String SEPARATOR = "; ";

    private List<String> messages;

    private boolean immediate;

    private String separator;

    BaseValidator(T value, boolean nullable, List<String> messages, String separator, boolean immediate) {
        super(value, nullable);
        this.separator = separator;
        this.immediate = immediate;
        this.messages = messages;
    }

    /*
     * -----------------------------------------------------------------
     * create messages
     * -----------------------------------------------------------------
     */

    /**
     * 添加一条错误信息，如果设置了立即结束将抛出异常
     *
     * @param message 消息内容
     *
     * @return this
     */
    final IMPL createMsg(String message) {
        if (immediate) {
            throw new IllegalArgumentException(message);
        } else {
            ensureMessages().add(message);
        }
        return current();
    }

    final IMPL createMsg(boolean tested, String message) { return tested ? current() : createMsg(message); }

    final IMPL createMsgOfCount(String message, int count) {
        return createMsg(message == null ? String.format("必须有 %d 项符合条件", count) : message);
    }

    final IMPL createMsgAtMost(String message, int count) {
        return createMsg(message == null ? String.format("最多只能有 %d 项符合条件", count) : message);
    }

    final IMPL createMsgAtLeast(String message, int count) {
        return createMsg(message == null ? String.format("至少需要有 %d 项符合条件", count) : message);
    }

    final IMPL createMsgCountOf(String message, int count) {
        return createMsg(message == null ? String.format("只能有 %d 项符合条件", count) : message);
    }

    /*
     * -----------------------------------------------------------------
     * inner member methods
     * -----------------------------------------------------------------
     */

    /**
     * 确保存在错误小消息集合，并返回
     *
     * @return 错误消息集合
     */
    final List<String> ensureMessages() { return messages == null ? (messages = new ArrayList<>()) : messages; }

    /**
     * 是否符合条件，在条件内执行
     * <p>
     * 目前总是执行
     *
     * @param handler 处理器
     *
     * @return 当前对象 this
     */
    final IMPL ifCondition(Function<T, IMPL> handler) { return handler.apply(value); }

    /**
     * 当前对象
     *
     * @return 当前对象
     */
    final IMPL current() { return (IMPL) this; }

    /*
     * -----------------------------------------------------------------
     * inner static methods
     * -----------------------------------------------------------------
     */

    final static <IMPL extends BaseValidator<M, IMPL>, M extends Map<K, V>, K, V> IMPL requireAtMostCountOf(
        IMPL impl, BiPredicate<? super K, ? super V> tester, int count, String message
    ) {
        return impl.ifCondition(value -> {
            int amount = 0;
            for (Map.Entry<K, V> item : value.entrySet()) {
                if (tester.test(item.getKey(), item.getValue()) && (++amount > count)) {
                    return impl.createMsgAtMost(message, count);
                }
            }
            return amount > count ? impl.createMsgAtMost(message, count) : impl;
        });
    }

    final static <IMPL extends BaseValidator<M, IMPL>, M extends Map<K, V>, K, V> IMPL requireAtLeastCountOf(
        IMPL impl, BiPredicate<? super K, ? super V> tester, int count, String message
    ) {
        return impl.ifCondition(value -> {
            int amount = 0;
            for (Map.Entry<K, V> item : value.entrySet()) {
                if (tester.test(item.getKey(), item.getValue()) && (++amount >= count)) {
                    return impl;
                }
            }
            return amount < count ? impl.createMsgAtLeast(message, count) : impl;
        });
    }

    final static <IMPL extends BaseValidator<M, IMPL>, M extends Map<K, V>, K, V> IMPL requireCountOf(
        IMPL impl, BiPredicate<? super K, ? super V> tester, int count, String message
    ) {
        return impl.ifCondition(value -> {
            int amount = 0;
            for (Map.Entry<K, V> item : value.entrySet()) {
                if (tester.test(item.getKey(), item.getValue()) && (++amount > count)) {
                    return impl.createMsgCountOf(message, count);
                }
            }
            return amount < count ? impl.createMsgCountOf(message, count) : impl;
        });
    }

    /*
     * -----------------------------------------------------------------
     * public methods
     * -----------------------------------------------------------------
     */

    /**
     * 错误消息集合
     *
     * @return 所有错误消息集合
     */
    public final List<String> getMessages() { return new ArrayList<>(ensureMessages()); }

    /**
     * 是否错误即时结束
     *
     * @return true: 立即结束； false: 最后统一结束
     */
    public final boolean isImmediate() { return immediate; }

    /**
     * 分隔符
     *
     * @return 分隔符
     */
    public final String getSeparator() { return separator; }

    /**
     * 获取错误信息，用默认分隔符
     *
     * @return 错误消息内容
     */
    public final String getMessage() { return getMessage(StringUtil.defaultIfNull(separator, SEPARATOR)); }

    /**
     * 所有错误消息组成的字符串，自定义分隔符
     *
     * @param separator 分隔符
     *
     * @return 错误消息内容
     */
    public final String getMessage(String separator) { return JoinerUtil.joinSkipNulls(ensureMessages(), separator); }

    @Override
    public final String toString() { return getMessage(); }

    @Override
    public final int hashCode() { return Objects.hashCode(value); }

    @Override
    public final boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        return Objects.equals(value, ((Value) obj).value);
    }

    /*
     * -----------------------------------------------------------------
     * get value or source
     * -----------------------------------------------------------------
     */

    /**
     * 验证通过，返回该对象，否则返回 null
     *
     * @return 数据对象，当符合验证时，返回源对象；否则返回 null
     */
    public final T nullIfInvalid() { return defaultIfInvalid(null); }

    /**
     * 验证通过，返回该对象，否则返回指定的默认值
     *
     * @param elseValue 不符合验证规则的默认值
     *
     * @return 数据对象，当符合验证时，返回源对象；否则返回自定义提供的对象
     */
    public final T defaultIfInvalid(T elseValue) { return isValid() ? value : elseValue; }

    /**
     * 验证通过，返回该对象，否则返回指定的默认值
     *
     * @param elseGetter 不符合验证规则的默认值
     *
     * @return 数据对象，当符合验证时，返回源对象；否则返回自定义提供的对象
     */
    public final T elseIfInvalid(Supplier<T> elseGetter) { return isValid() ? value : elseGetter.get(); }

    /**
     * 验证通过，返回该对象，否则抛出异常
     *
     * @return 符合验证规则的原对象
     *
     * @throws IllegalArgumentException 如果验证不通过，抛出这个异常，异常内容为所有验证不通过规则的内容
     */
    @Override
    public final T get() {
        if (isValid()) {
            return value;
        }
        throw new IllegalArgumentException(getMessage());
    }

    /**
     * 验证通过，返回该对象，否则抛出指定信息异常
     *
     * @param errorMessage 指定错误消息
     *
     * @return 符合验证规则的原对象
     *
     * @throws IllegalArgumentException 如果验证不通过，抛出异常
     */
    public final T get(String errorMessage) {
        if (isValid()) {
            return value;
        }
        throw new IllegalArgumentException(errorMessage);
    }

    /**
     * 验证通过，返回该对象，否则抛出指定异常
     *
     * @param exceptionBuilder 异常构建器
     * @param <EX>             异常类型
     *
     * @return 符合验证的目标对象
     *
     * @throws EX 异常
     */
    public final <EX extends Throwable> T get(Function<String, EX> exceptionBuilder) throws EX {
        if (isValid()) {
            return value;
        }
        throw exceptionBuilder.apply(toString());
    }

    /*
     * -----------------------------------------------------------------
     * defaults and presets
     * -----------------------------------------------------------------
     */

    /**
     * 是否验证通过
     *
     * @return true：验证通过；false：验证不通过
     */
    public final boolean isValid() { return messages == null || messages.isEmpty(); }

    /**
     * 是否验证不通过
     *
     * @return true：验证不通过；false：验证通过
     */
    public final boolean isInvalid() { return !isValid(); }

    /**
     * 当验证通过时执行处理
     *
     * @param consumer 处理器
     *
     * @return 当前 Validator 对象
     */
    public IMPL ifValid(Consumer<? super T> consumer) {
        if (isValid()) {
            consumer.accept(value);
        }
        return current();
    }

    /**
     * 当验证不通过时执行处理
     *
     * @param consumer 处理器
     *
     * @return 当前 Validator 对象
     */
    public IMPL ifInvalid(Consumer<? super T> consumer) {
        if (isInvalid()) {
            consumer.accept(value);
        }
        return current();
    }

    /**
     * 直接添加一条错误消息
     *
     * @param message 消息内容
     *
     * @return 当前 Validator 实例
     */
    @Override
    public IMPL addErrorMessage(String message) { return createMsg(message); }

    /**
     * 可用于在后面条件验证前预先设置一部分默认值
     * <p>
     * 只能改变{@link #value}内部属性的值，不能重新设置 value
     *
     * @param consumer 预处理器
     *
     * @return 当前 Validator 实例
     */
    public IMPL preset(Consumer<? super T> consumer) {
        consumer.accept(value);
        return current();
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
    public IMPL setImmediate(boolean immediate) {
        this.immediate = immediate;
        return current();
    }

    /**
     * 设置错误信息分隔符，不能为 null
     *
     * @param separator 错误消息分割符
     *
     * @return 当前 Validator 实例
     */
    public IMPL setSeparator(String separator) {
        this.separator = separator;
        return current();
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
    public IMPL when(Predicate<? super T> tester, Consumer<IMPL> scopedValidator) {
        if (tester.test(getValue())) {
            scopedValidator.accept(current());
        }
        return current();
    }

    /*
     * -----------------------------------------------------------------
     * requires
     * -----------------------------------------------------------------
     */

    /**
     * 要求符合条件
     *
     * @param tester  断言函数
     * @param message 错误消息
     *
     * @return 当前 Validator 对象
     */
    @Override
    public IMPL require(Predicate<? super T> tester, String message) {
        return ifCondition(value -> createMsg(tester.test(value), message));
    }
}
