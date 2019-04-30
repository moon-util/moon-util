package com.moon.core.util.iterator;

import java.util.Iterator;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public class ObjectsIterator<T>
    extends BaseArrayIterator
    implements Iterator<T> {

    private final T[] array;

    public ObjectsIterator(T[] array) {
        super(array == null ? 0 : array.length);
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        return this.index < this.length;
    }


    @Override
    public T next() {
        return this.array[index++];
    }
}
