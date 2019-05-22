package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public class LongsIterator
    extends BaseArrayIterator
    implements Iterator<Long> {

    private final long[] array;

    public LongsIterator(long[] array) {
        super(array == null ? 0 : array.length);
        this.array = array;
    }

    @Override
    public boolean hasNext() { return this.index < this.length; }

    @Override
    public Long next() { return this.array[index++]; }
}
