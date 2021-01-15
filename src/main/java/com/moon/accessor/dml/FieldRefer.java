package com.moon.accessor.dml;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface FieldRefer<TB, F> {

    /**
     * 返回引用的字段
     *
     * @param table 表
     *
     * @return 表字段
     */
    F ref(TB table);
}
