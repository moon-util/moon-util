package com.moon.accessor.meta;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface PropertyGetter<R, T> {

    /**
     * 获取 record 中某属性的值
     *
     * @param record 数据实体
     *
     * @return 数据实体某字段值
     */
    T get(R record);
}
