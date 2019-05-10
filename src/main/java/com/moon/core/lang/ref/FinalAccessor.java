package com.moon.core.lang.ref;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public class FinalAccessor<T> {

    private T value;

    public FinalAccessor() { }

    public FinalAccessor(T value) { this.set(value); }

    public static <T> FinalAccessor<T> of() { return new FinalAccessor<>(); }

    public static <T> FinalAccessor<T> of(T value) { return new FinalAccessor<>(value); }

    /*
     * ------------------------------------------------------------
     * sets
     * ------------------------------------------------------------
     */

    public FinalAccessor<T> clear() {
        this.value = null;
        return this;
    }

    public FinalAccessor<T> set(T value) {
        this.value = value;
        return this;
    }

    public FinalAccessor<T> set(Supplier<T> supplier) {
        this.value = supplier.get();
        return this;
    }

    public FinalAccessor<T> setIfAbsent(T value) { return isPresent() ? this : set(value); }

    public FinalAccessor<T> setIfAbsent(Supplier<T> supplier) { return isPresent() ? this : set(supplier.get()); }

    /*
     * ------------------------------------------------------------
     * gets
     * ------------------------------------------------------------
     */

    public T get() { return value; }

    public T getOrDefault(T defaultValue) { return isPresent() ? value : defaultValue; }

    public T getOrDefault(Supplier<T> supplier) { return isPresent() ? value : supplier.get(); }

    public T getOrThrow() { return Objects.requireNonNull(value); }

    public T getOrThrow(String exceptionMessage) { return Objects.requireNonNull(value, exceptionMessage); }

    public <EX extends Throwable> T getOrThrow(EX ex) {
        if (isAbsent()) {
            throw new IllegalArgumentException(ex);
        }
        return value;
    }

    public <EX extends Throwable> T getOrThrow(Supplier<EX> supplier) {
        if (isAbsent()) {
            throw new IllegalArgumentException(supplier.get());
        }
        return value;
    }

    /*
     * ------------------------------------------------------------
     * assertions
     * ------------------------------------------------------------
     */

    public boolean isPresent() { return value != null; }

    public boolean isAbsent() { return value == null; }

    public boolean isEquals(Object value) { return Objects.equals(value, this.value); }

    /*
     * ------------------------------------------------------------
     * consumers
     * ------------------------------------------------------------
     */

    public FinalAccessor<T> ifPresent(Consumer<T> consumer) {
        if (isPresent()) {
            consumer.accept(value);
        }
        return this;
    }

    public FinalAccessor<T> ifPresentOrThrow(Consumer<T> consumer) {
        consumer.accept(Objects.requireNonNull(value));
        return this;
    }

    public FinalAccessor<T> ifPresentOrThrow(Consumer<T> consumer, String message) {
        consumer.accept(Objects.requireNonNull(value, message));
        return this;
    }

    public <EX extends Throwable> FinalAccessor<T> ifPresentOrThrow(
        Consumer<T> consumer, Supplier<EX> supplier) throws EX {
        if (isPresent()) {
            consumer.accept(value);
        } else {
            throw supplier.get();
        }
        return this;
    }

    /*
     * ------------------------------------------------------------
     * computer
     * ------------------------------------------------------------
     */

    public FinalAccessor<T> compute(Function<T, T> computer) {
        value = computer.apply(value);
        return this;
    }

    public FinalAccessor<T> computeIfPresent(Function<T, T> computer) {
        if (isPresent()) {
            value = computer.apply(value);
        }
        return this;
    }

    /*
     * ------------------------------------------------------------
     * mappers
     * ------------------------------------------------------------
     */

    public <O> FinalAccessor<O> transform(Function<T, O> function) { return of(function.apply(value)); }

    @Override
    public String toString() { return String.valueOf(value); }
}
