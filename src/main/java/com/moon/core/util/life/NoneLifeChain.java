package com.moon.core.util.life;

/**
 * @author moonsky
 */
public interface NoneLifeChain extends BeforeLifeChain<Object>, AfterLifeChain<Object> {

    /**
     * 是否有前置执行
     *
     * @return
     */
    @Override
    default boolean hasBefore() { return false; }

    /**
     * 是否有后置执行
     *
     * @return
     */
    @Override
    default boolean hasAfter() { return false; }

    /**
     * 前置执行
     *
     * @param param 入参
     *
     * @return 处理后的入参
     */
    @Override
    default Object before(Object param) { return param; }

    /**
     * 后置执行
     *
     * @param result 返回值
     *
     * @return 处理后的返回值
     */
    @Override
    default Object after(Object result) { return result; }
}
