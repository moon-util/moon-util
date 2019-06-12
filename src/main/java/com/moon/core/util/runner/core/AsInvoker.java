package com.moon.core.util.runner.core;

/**
 * @author benshaoye
 */
interface AsInvoker extends AsValuer {

    /**
     * 是否是一个方法执行
     *
     * @return
     */
    @Override
    default boolean isInvoker() { return true; }

    /**
     * 是否是一个静态方法执行
     *
     * @return
     */
    default boolean isStaticInvoker() { return false; }

    /**
     * 是否是一个成员方法执行
     *
     * @return
     */
    default boolean isMemberInvoker() { return false; }
}
