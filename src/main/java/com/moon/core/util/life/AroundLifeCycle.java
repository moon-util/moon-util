package com.moon.core.util.life;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface AroundLifeCycle<T> extends LifeCycle<T> {
    /**
     * 没有任何
     */
    AroundLifeCycle EMPTY = EmptyLifeCycle.VALUE;

    /**
     * 环绕主任务执行
     * <p>
     * 在before之后，after之前
     *
     * @param item
     * @param point
     */
    @Override
    void around(T item, CutPoint point);

    /**
     * 主任务执行前执行
     *
     * @param item
     */
    @Override
    default void before(T item) {}

    /**
     * 主任务执行后执行
     *
     * @param item
     */
    @Override
    default void after(T item) {}
}
