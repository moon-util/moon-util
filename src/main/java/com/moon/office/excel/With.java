package com.moon.office.excel;

import java.util.function.Function;

/**
 * @author benshaoye
 */
public class With<T> {

    private final static With DEFAULT = new With();

    private T data;

    public With() {}

    public With(T data) { this.data = data; }

    public final static <T> With<T> of(T data) { return new With<>(data); }

    public final static <T> With<T> of(Class<T> data) { return DEFAULT; }

    @Override
    public String toString() {return String.valueOf(data);}

    public String toString(Function<? super T, String> computer) { return toString(data, computer); }

    public String toString(T data, Function<? super T, String> computer) { return computer.apply(data); }

    public Integer toInt(Function<? super T, Integer> computer) { return toInt(data, computer); }

    public Integer toInt(T data, Function<? super T, Integer> computer) { return computer.apply(data); }

    public Long toLong(Function<? super T, Long> computer) { return toLong(data, computer); }

    public Long toLong(T data, Function<? super T, Long> computer) { return computer.apply(data); }

    public Double toDouble(Function<? super T, Double> computer) { return toDouble(data, computer); }

    public Double toDouble(T data, Function<? super T, Double> computer) { return computer.apply(data); }

    public Boolean toBoolean(Function<? super T, Boolean> computer) { return toBoolean(data, computer); }

    public Boolean toBoolean(T data, Function<? super T, Boolean> computer) { return computer.apply(data); }

    public <R> R to(Function<? super T, ? extends R> computer) { return to(data, computer); }

    public <R> R to(T data, Function<? super T, ? extends R> computer) { return computer.apply(data); }
}
