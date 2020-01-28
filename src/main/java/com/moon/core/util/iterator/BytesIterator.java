package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author benshaoye
 */
public class BytesIterator
    extends BaseArrayIterator
    implements Iterator<Byte> {

    private final byte[] array;

    public BytesIterator(byte[] array) {
        super(array == null ? 0 : array.length);
        this.array = array;
    }

    @Override
    public boolean hasNext() { return this.index < this.length; }

    @Override
    public Byte next() { return this.array[index++]; }
}
