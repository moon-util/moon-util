package com.moon.core.util.runner.core;

import com.moon.core.util.runner.Runner;

/**
 * @author benshaoye
 */
@FunctionalInterface
interface AsRunner extends Runner {
    /**
     * 使用外部数据
     *
     * @param data
     * @return
     */
    @Override
    default Object run(Object data) { throw new UnsupportedOperationException(); }

    /**
     * 计算
     *
     * @param left
     * @param right
     * @return
     */
    default Object exe(Object right, Object left) { throw new UnsupportedOperationException(); }

    /**
     * 计算
     *
     * @param right
     * @param left
     * @param data
     * @return
     */
    Object exe(AsRunner right, AsRunner left, Object data);

    /**
     * 运算符优先级
     *
     * @return
     */
    default int getPriority() { return 99; }

    /*
     * --------------------------------------
     * 判断
     * --------------------------------------
     */

    /**
     * 计算器
     *
     * @return
     */
    default boolean isHandler() { return false; }

    /**
     * 取值器
     *
     * @return
     */
    default boolean isValuer() { return isConst() || isGetter(); }

    /**
     * 普通常量
     *
     * @return
     */
    default boolean isConst() { return false; }

    /**
     * 是否使用外部数据
     *
     * @return
     */
    default boolean isGetter() { return false; }

    /**
     * 是否是赋值器
     *
     * @return
     */
    default boolean isSetter() { return false; }

    /**
     * 是否是一个方法执行
     *
     * @return
     */
    default boolean isInvoker() { return false; }
}
