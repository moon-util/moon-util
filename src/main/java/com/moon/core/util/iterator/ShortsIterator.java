package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author benshaoye
 */
public class ShortsIterator
    extends BaseArrayIterator
    implements Iterator<Short> {

    private final short[] array;

    public ShortsIterator(short[] array) {
        super(array == null ? 0 : array.length);
        this.array = array;
    }

    @Override
    public boolean hasNext() { return this.index < this.length; }

    @Override
    public Short next() { return this.array[index++]; }
}
