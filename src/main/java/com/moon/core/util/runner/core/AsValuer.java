package com.moon.core.util.runner.core;

/**
 * @author benshaoye
 */
interface AsValuer extends AsRunner {
    /**
     * 取值器
     *
     * @return
     */
    @Override
    default boolean isValuer() { return true; }

    /**
     * 计算
     *
     * @param left
     * @param right
     * @param data
     * @return
     */
    @Override
    default Object exe(AsRunner left, AsRunner right, Object data) { throw new UnsupportedOperationException(); }
}
