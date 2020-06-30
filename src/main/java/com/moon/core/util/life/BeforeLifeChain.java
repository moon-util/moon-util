package com.moon.core.util.life;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface BeforeLifeChain<P> {

    /**
     * 是否有前置执行
     *
     * @return
     */
    default boolean hasBefore() { return false; }

    /**
     * 前置执行
     *
     * @param param 入参
     *
     * @return 处理后的入参
     */
    <S extends P> S before(S param);
}
