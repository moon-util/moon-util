package com.moon.core.util.life;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface AfterLifeChain<R> {

    /**
     * 后置执行
     *
     * @param result 返回值
     *
     * @return 处理后的返回值
     */
    <S extends R> S after(S result);

    /**
     * 是否有后置执行
     *
     * @return
     */
    default boolean hasAfter() { return true; }
}
