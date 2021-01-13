package com.moon.accessor;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface PropertySetter<R, T> {

    /**
     * 设置 record 中某属性的值
     *
     * @param record 数据实体
     * @param value  字段值
     */
    void set(R record, T value);
}
