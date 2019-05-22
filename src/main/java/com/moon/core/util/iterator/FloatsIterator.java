package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public class FloatsIterator
    extends BaseArrayIterator
    implements Iterator<Float> {

    private final float[] array;

    public FloatsIterator(float[] array) {
        super(array == null ? 0 : array.length);
        this.array = array;
    }

    @Override
    public boolean hasNext() { return this.index < this.length; }

    @Override
    public Float next() { return this.array[index++]; }
}
