package com.moon.spring.data.redis;

import java.util.function.Consumer;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface ExceptionHandler extends Consumer<Exception> {

    /**
     * 当出现异常的时候
     *
     * @param ex 操作 redis 过程中捕获的异常
     */
    void onException(Exception ex);

    /**
     * 参考{@link #onException(Exception)}
     *
     * @param e 操作 redis 过程中捕获的异常
     */
    @Override
    default void accept(Exception e) { onException(e);}
}
