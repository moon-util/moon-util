package com.moon.more.excel.annotation;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface TableColumnTransformer<F, R> {

    /**
     * 返回转换后的值
     *
     * @param fieldValue 字段值
     *
     * @return 转换后的值
     */
    R transform(F fieldValue);
}
