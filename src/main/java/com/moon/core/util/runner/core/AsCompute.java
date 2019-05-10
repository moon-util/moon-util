package com.moon.core.util.runner.core;

/**
 * @author benshaoye
 */
interface AsCompute extends AsRunner {
    /**
     * 计算器
     *
     * @return
     */
    @Override
    default boolean isHandler() { return true; }

    /**
     * 计算
     *
     * @param right
     * @param left
     * @param data
     * @return
     */
    @Override
    default Object exe(AsRunner right, AsRunner left, Object data) { return exe(right.run(data), left.run(data)); }
}
