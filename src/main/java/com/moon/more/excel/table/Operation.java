package com.moon.more.excel.table;

/**
 * @author benshaoye
 */
public interface Operation<T> {

    /**
     * 创建实例
     *
     * @param thisObject
     *
     * @return
     */
    T newInstance(Object thisObject);
}
