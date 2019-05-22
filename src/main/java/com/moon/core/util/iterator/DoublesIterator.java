package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public class DoublesIterator
    extends BaseArrayIterator
    implements Iterator<Double> {

    private final double[] array;

    public DoublesIterator(double[] array) {
        super(array == null ? 0 : array.length);
        this.array = array;
    }

    @Override
    public boolean hasNext() { return this.index < this.length; }

    @Override
    public Double next() { return this.array[index++]; }
}
