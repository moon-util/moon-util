package com.moon.redis;

import java.util.function.Consumer;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface ExceptionHandler extends Consumer<Exception> {

    /**
     * 当出现异常的时候
     *
     * @param ex
     */
    void onException(Exception ex);

    /**
     * 参考{@link #onException(Exception)}
     *
     * @param e
     */
    @Override
    default void accept(Exception e) { onException(e);}
}
