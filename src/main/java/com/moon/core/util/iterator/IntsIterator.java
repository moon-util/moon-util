package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author benshaoye
 */
public class IntsIterator
    extends BaseArrayIterator
    implements Iterator<Integer> {

    private final int[] array;

    public IntsIterator(int[] array) {
        super(array == null ? 0 : array.length);
        this.array = array;
    }

    @Override
    public boolean hasNext() { return this.index < this.length; }

    @Override
    public Integer next() { return this.array[index++]; }
}
