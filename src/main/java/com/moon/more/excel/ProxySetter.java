package com.moon.more.excel;

/**
 * @author benshaoye
 */
abstract class ProxySetter<T, K> extends BaseProxy<K> {

    protected ProxySetter(K obj) { super(obj); }

    /**
     * set info to t
     *
     * @param t
     */
    abstract void setup(T t);
}
