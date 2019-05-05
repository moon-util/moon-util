package com.moon.core.util.life;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author benshaoye
 */
public interface LifeCycle<T> {
    /**
     * 没有任何
     */
    LifeCycle EMPTY = EmptyLifeCycle.VALUE;

    /**
     * 执行
     *
     * @param value
     * @param consumer
     * @param cycle
     * @param <T>
     */
    static <T> void runAccept(T value, Consumer<T> consumer, LifeCycle<T> cycle) {
        LifeUtil.runAccept(value, consumer, cycle);
    }

    /**
     * 执行
     *
     * @param value
     * @param function
     * @param cycle
     * @param <T>
     * @param <R>
     * @return
     */
    static <T, R> R runApply(T value, Function<T, R> function, LifeCycle<T> cycle) {
        return LifeUtil.runApply(value, function, cycle);
    }

    /**
     * 返回一个默认{@link BeforeLifeCycle}，用于支持 lambda 表达式
     *
     * @param cycle
     * @param <E>
     * @return
     */
    static <E> LifeCycle<E> before(BeforeLifeCycle<E> cycle) { return cycle; }

    /**
     * 返回一个默认{@link AfterLifeCycle}，用于支持 lambda 表达式
     *
     * @param cycle
     * @param <E>
     * @return
     */
    static <E> LifeCycle<E> after(AfterLifeCycle<E> cycle) { return cycle; }

    /**
     * 返回一个默认{@link AroundLifeCycle}，用于支持 lambda 表达式
     *
     * @param cycle
     * @param <E>
     * @return
     */
    static <E> LifeCycle<E> around(AroundLifeCycle<E> cycle) { return cycle; }

    /**
     * 主任务执行前执行
     *
     * @param item
     */
    void before(T item);

    /**
     * 主任务执行后执行
     *
     * @param item
     */
    void after(T item);

    /**
     * 环绕主任务执行
     * <p>
     * 在before之后，after之前
     *
     * @param item
     * @param point
     */
    void around(T item, CutPoint point);

    /**
     * 在 before 执行前对数据进行一次计算，并使用计算后的值
     *
     * @param item
     * @return
     */
    default T computeWhenCutIn(T item) { return item; }
}
