package com.moon.data.jdbc.processing;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author benshaoye
 */
final class Nameable {

    private final AtomicInteger indexer = new AtomicInteger();

    public Nameable() {}

    public String nextField() {
        return "var" + indexer.getAndIncrement();
    }

    public String nextStaticField() {
        return "var" + indexer.getAndIncrement();
    }
}
