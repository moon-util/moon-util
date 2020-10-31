package com.moon.core.util.concurrent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author moonsky
 */
public class ConcurrentLinkedHashMap<K, V> extends ConcurrentHashMap<K, V> {

    public ConcurrentLinkedHashMap() {
    }

    static class Element<K, V> {

        Element<K, V> before, after;
    }
}
