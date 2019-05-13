package com.moon.core.util.function;

import com.moon.core.exception.DefaultException;

/**
 * @author benshaoye
 * @see java.util.function.Supplier
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {
    /**
     * 运行并获取值
     *
     * @return
     * @throws Throwable
     */
    T get() throws Throwable;

    /**
     * 应用并返回，如果异常，将包装成非检查异常抛出
     *
     * @return
     */
    default T orWithUnchecked() {
        try {
            return get();
        } catch (Throwable t) {
            return DefaultException.doThrow(t);
        }
    }
}
