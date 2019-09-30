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
abstract class BaseValidator<T, IMPL extends BaseValidator> extends Value<T>
    implements Cloneable, Serializable, IValidator<T, IMPL>, Supplier<T> {

    static final long serialVersionUID = 1L;

    static final String SEPARATOR = ", ";

    private final IMPL parent = null;

    private List<String> messages;

    private boolean immediate;

    private String separator;

    private boolean condition;

    BaseValidator(T value, boolean nullable, List<String> messages, String separator, boolean immediate) {
        super(value, nullable);
        this.separator = separator;
        this.immediate = immediate;
        this.messages = messages;
        this.end();
    }

    /*
     * -----------------------------------------------------------------
     * create messages
     * -----------------------------------------------------------------
     */

    /**
     * 添加一条错误信息，如果设置了立即结束将抛出异常
     *
     * @param message
     *
     * @return
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

    final List<String> ensureMessages() { return messages == null ? (messages = new ArrayList<>()) : messages; }

    final IMPL ifCondition(Function<T, IMPL> handler) { return condition ? handler.apply(value) : current(); }

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

    public final List<String> getMessages() { return new ArrayList<>(ensureMessages()); }

    public final boolean isImmediate() { return immediate; }

    public final String getSeparator() { return separator; }

    /**
     * 获取错误信息，用默认分隔符
     *
     * @return
     */
    public final String getMessage() { return getMessage(StringUtil.defaultIfNull(separator, SEPARATOR)); }

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
     * getSheet value or source
     * -----------------------------------------------------------------
     */

    /**
     * 验证通过，返回该对象，否则返回 null
     *
     * @return
     */
    public final T nullIfInvalid() { return defaultIfInvalid(null); }

    /**
     * 验证通过，返回该对象，否则返回指定的默认值
     *
     * @param elseValue
     *
     * @return
     */
    public final T defaultIfInvalid(T elseValue) { return isValid() ? value : elseValue; }

    /**
     * 验证通过，返回该对象，否则返回指定的默认值
     *
     * @param elseGetter
     *
     * @return
     */
    public final T elseIfInvalid(Supplier<T> elseGetter) { return isValid() ? value : elseGetter.get(); }

    /**
     * 验证通过，返回该对象，否则抛出异常
     *
     * @return
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
     * @param errorMessage
     *
     * @return
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
     * @param exception
     * @param <EX>
     *
     * @return
     *
     * @throws EX
     */
    public final <EX extends Throwable> T get(Function<String, EX> exception) throws EX {
        if (isValid()) {
            return value;
        }
        throw exception.apply(toString());
    }

    /*
     * -----------------------------------------------------------------
     * defaults and presets
     * -----------------------------------------------------------------
     */

    @Override
    public final IMPL addErrorMessage(String message) { return createMsg(message); }

    /**
     * 可用于在后面条件验证前预先设置一部分默认值
     *
     * @param consumer
     *
     * @return
     */
    public final IMPL preset(Consumer<? super T> consumer) {
        consumer.accept(value);
        return current();
    }

    /**
     * 设置是否立即终止
     *
     * @param immediate
     *
     * @return
     */
    public final IMPL setImmediate(boolean immediate) {
        this.immediate = immediate;
        return current();
    }

    /**
     * 设置错误信息分隔符，不能为 null
     *
     * @param separator
     *
     * @return
     */
    public final IMPL setSeparator(String separator) {
        this.separator = separator;
        return current();
    }

    public final boolean isValid() { return messages == null || messages.isEmpty(); }

    public final boolean isInvalid() { return !isValid(); }

    public final IMPL ifValid(Consumer<? super T> consumer) {
        if (isValid()) {
            consumer.accept(value);
        }
        return current();
    }

    public final IMPL ifInvalid(Consumer<? super T> consumer) {
        if (isInvalid()) {
            consumer.accept(value);
        }
        return current();
    }

    @Override
    public final IMPL ifWhen(Consumer<? super T> consumer) {
        if (condition) {
            consumer.accept(value);
        }
        return current();
    }

    /*
     when() 与 end() 连用，
     使用 when() 时，需要传入一个验证器，当内容满足验证条件时，
     将继续执行后面的验证规则，否则将调跳过后续的验证规则，直到 end() 为止
     */

    @Override
    public final IMPL when(Predicate<? super T> tester) {
        condition = tester.test(value);
        return current();
    }

    @Override
    public final IMPL end() { return when(o -> true); }

    /*
     * -----------------------------------------------------------------
     * requires
     * -----------------------------------------------------------------
     */

    @Override
    public final IMPL require(Predicate<? super T> tester, String message) {
        return ifCondition(value -> createMsg(tester.test(value), message));
    }
}
