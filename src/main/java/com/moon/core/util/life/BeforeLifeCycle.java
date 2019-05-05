package com.moon.core.util.life;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface BeforeLifeCycle<T> extends LifeCycle<T> {
    /**
     * 没有任何
     */
    BeforeLifeCycle EMPTY = EmptyLifeCycle.VALUE;

    /**
     * 主任务执行前执行
     *
     * @param item
     */
    @Override
    void before(T item);

    /**
     * 主任务执行后执行
     *
     * @param item
     */
    @Override
    default void after(T item) {}

    /**
     * 环绕主任务执行
     * <p>
     * 在before之后，after之前
     *
     * @param item
     * @param point
     */
    @Override
    default void around(T item, CutPoint point) {}
}
