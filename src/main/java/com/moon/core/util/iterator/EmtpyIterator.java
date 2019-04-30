package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author ZhangDongMin
 * @date 2018/3/9 16:20
 */
public enum EmtpyIterator implements Iterator {

    DEFAULT;
    /**
     * 为什么要定义这个变量？
     * <p>
     * 因为 EMPTY 这个变量名在具体使用的时候可能已经被定义了
     */
    public static final EmtpyIterator VALUE = DEFAULT;
    public static final EmtpyIterator empty = VALUE;
    public static final EmtpyIterator EMPTY = empty;

    @Override
    public final boolean hasNext() {
        return false;
    }

    @Override
    public final Object next() {
        throw new UnsupportedOperationException();
    }
}
