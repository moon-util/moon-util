package com.moon.more.excel;

/**
 * @author benshaoye
 */
abstract class ProxyBuilder<FROM, R> extends BaseProxy<String> {

    protected ProxyBuilder(String key) { super(key); }

    /**
     * build a R
     *
     * @param from
     *
     * @return
     */
    abstract R build(FROM from);
}
