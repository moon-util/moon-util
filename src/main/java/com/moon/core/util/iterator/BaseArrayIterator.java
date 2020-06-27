package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author benshaoye
 */
abstract class BaseArrayIterator {

    protected final static Iterator EMPTY = EmptyIterator.EMPTY;

    protected int index = 0;
    protected final int length;

    protected BaseArrayIterator(int length) { this.length = length; }
}
