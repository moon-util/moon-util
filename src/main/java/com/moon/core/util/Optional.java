package com.moon.core.util;

import com.moon.core.lang.Executable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.moon.core.lang.ThrowUtil.doThrow;
import static com.moon.core.util.OptionalImpl.EMPTY;

/**
 * @author benshaoye
 */
public interface Optional<T> extends Optionally {

    /**
     * null 对象
     *
     * @return
     */
    static Optional empty() { return EMPTY; }

    /**
     * 新对象，value 不可为 null
     *
     * @param value
     * @param <E>
     * @return
     */
    static <E> Optional<E> of(E value) { return of(value, OptionalImpl::new); }

    /**
     * 新对象，可为 null
     *
     * @param value
     * @param <E>
     * @return
     */
    static <E> Optional<E> ofNullable(E value) { return value == null ? EMPTY : of(value); }

    /**
     * 构造对象
     *
     * @param value
     * @param mapper
     * @param <E>
     * @return
     */
    static <E> Optional<E> of(E value, Function<? super E, ? extends Optional<E>> mapper) {
        return mapper.apply(value);
    }

    /**
     * 设置值，可为 null
     *
     * @param value
     * @return
     */
    default Optional<T> setNullable(T value) { return this; }

    /**
     * 设置 null 值
     *
     * @return
     */
    default Optional<T> setNull() { return setNullable(null); }

    /**
     * 设置值，不可为 null
     *
     * @param value
     * @return
     */
    default Optional<T> set(T value) { return value == null ? doThrow(null) : setNullable(value); }

    /**
     * 返回值
     *
     * @return
     */
    default T getOrNull() { return null; }

    /**
     * 返回值或抛出异常
     *
     * @return
     */
    default T get() { return isPresent() ? getOrNull() : doThrow(null); }

    /**
     * 返回值或使用默认值
     *
     * @param defaultValue
     * @return
     */
    default T getOrDefault(T defaultValue) { return isPresent() ? getOrNull() : defaultValue; }

    /**
     * 返回值或使用默认值
     *
     * @param supplier
     * @return
     */
    default T getOrElse(Supplier<T> supplier) { return isPresent() ? getOrNull() : supplier.get(); }

    /**
     * 返回值或抛出指定异常
     *
     * @param supplier
     * @param <EX>
     * @return
     */
    default <EX extends Throwable> T getOrThrow(Supplier<EX> supplier) throws EX {
        if (isPresent()) { return getOrNull(); }
        throw supplier.get();
    }

    /**
     * 是否存在
     *
     * @return
     */
    @Override
    default boolean isPresent() { return getOrNull() != null; }

    /**
     * 是否不存在
     *
     * @return
     */
    @Override
    default boolean isAbsent() { return getOrNull() == null; }

    /**
     * 确保符合条件，否则返回 null 对象
     *
     * @param predicate
     * @return
     */
    default Optional<T> filter(Predicate<? super T> predicate) {
        return isAbsent() ? this : (predicate.test(getOrNull()) ? this : EMPTY);
    }

    /**
     * 不存在的情况设置默认值
     *
     * @param supplier
     * @return
     */
    default Optional<T> elseIfAbsent(Supplier<T> supplier) { return isAbsent() ? setNullable(supplier.get()) : this; }

    /**
     * 不存在的情况设置默认值
     *
     * @param defaultValue
     * @return
     */
    default Optional<T> defaultIfAbsent(T defaultValue) { return isAbsent() ? setNullable(defaultValue) : this; }

    /**
     * 存在的情况下消费
     *
     * @param consumer
     * @return
     */
    default Optional<T> ifPresent(Consumer<? super T> consumer) {
        if (isPresent()) { consumer.accept(getOrNull()); }
        return this;
    }

    /**
     * 不存在的情况下执行
     *
     * @param executor
     * @return
     */
    default Optional<T> ifAbsent(Executable executor) {
        if (isAbsent()) { executor.execute(); }
        return this;
    }

    /**
     * 计算
     *
     * @param computer
     * @return
     */
    default <E> E compute(Function<? super T, ? extends E> computer) { return computer.apply(getOrNull()); }

    /**
     * 转换
     *
     * @param computer
     * @param <E>
     * @return
     */
    default <E> Optional<E> transform(Function<? super T, ? extends E> computer) { return ofNullable(computer.apply(getOrNull())); }
}
