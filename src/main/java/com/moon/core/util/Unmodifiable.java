package com.moon.core.util;


/**
 * @author ZhangDongMin
 * @date 2018/9/11
 */
@FunctionalInterface
public interface Unmodifiable<T> {
    /**
     * 调整为不可修改
     *
     * @return
     */
    Unmodifiable<T> flipToUnmodify();
}
