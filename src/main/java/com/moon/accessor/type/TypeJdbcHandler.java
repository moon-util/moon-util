package com.moon.accessor.type;

/**
 * @author benshaoye
 */
public interface TypeJdbcHandler<T> extends TypeHandler<T> {

    /**
     * 支持的 java 类型
     *
     * @return java 实体类
     */
    Class<T> supportClass();

    /**
     * 支持的 jdbc type
     *
     * @return jdbc types
     */
    int[] supportJdbcTypes();
}
