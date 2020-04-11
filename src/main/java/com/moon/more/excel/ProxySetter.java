package com.moon.more.excel;

import java.util.function.Consumer;

/**
 * @author benshaoye
 */
abstract class ProxySetter<T, K> extends BaseProxy<K> implements Consumer<T> {

    protected ProxySetter(K obj) { super(obj); }

    /**
     * set info to t
     *
     * @param t
     */
    abstract void setup(T t);

    /**
     * set info to t
     *
     * @param t
     */
    @Override
    public void accept(T t) { setup(t); }
}
