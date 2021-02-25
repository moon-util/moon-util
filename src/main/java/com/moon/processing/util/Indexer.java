package com.moon.processing.util;

/**
 * @author benshaoye
 */
public class Indexer {

    private int index;

    public Indexer() {this(0);}

    public Indexer(int start) { this.index = start; }

    public int incrementAndGet() { return ++index; }

    public int getAndIncrement() { return index++; }

    public boolean eq(int value) { return index == value; }

    public boolean gt(int value) { return index > value; }
}
