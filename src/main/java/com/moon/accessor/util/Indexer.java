package com.moon.accessor.util;

/**
 * @author benshaoye
 */
public final class Indexer {

    private final int length;
    private int index;

    public Indexer(int length) { this(0, length); }

    public Indexer(int index, int length) {
        this.index = index;
        this.length = length;
    }

    public int get() { return index; }

    public void set(int index) { this.index = index; }

    public void incr() { index++; }

    public void decr() { index--; }

    public int next() { return index++; }

    public boolean hasNext() { return index < length; }

}
