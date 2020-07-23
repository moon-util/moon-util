package com.moon.core.util.concurrent;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author moonsky
 */
public class SynchronizationFuture<V> implements Future<V> {

    private final V value;

    public SynchronizationFuture() { this(null); }

    public SynchronizationFuture(V value) {
        this.value = value;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) { return false; }

    @Override
    public boolean isCancelled() { return false; }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public V get() { return value; }

    @Override
    public V get(long timeout, TimeUnit unit) { return value; }
}
