package com.moon.processor.holder;

import java.io.Closeable;
import java.util.LinkedList;

/**
 * @author benshaoye
 */
public class Closer implements Closeable {

    private final LinkedList<AutoCloseable> closes = new LinkedList<>();

    public Closer() {}

    public static Closer of() { return new Closer(); }

    public <T extends AutoCloseable> T add(T closeable) {
        if (closeable != null) {
            this.closes.addFirst(closeable);
        }
        return closeable;
    }

    @Override
    public void close() {
        for (AutoCloseable closeable : this.closes) {
            try {
                closeable.close();
            } catch (Throwable ignored) {}
        }
    }
}
