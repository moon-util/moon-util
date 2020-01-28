package com.moon.core.util.iterator;

/**
 * @author benshaoye
 */
abstract class BaseArrayIterator {

    protected int index = 0;
    protected final int length;

    protected BaseArrayIterator(int length) { this.length = length; }
}
