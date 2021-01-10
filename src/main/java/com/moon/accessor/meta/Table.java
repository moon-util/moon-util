package com.moon.accessor.meta;

/**
 * @author benshaoye
 */
public interface Table<R> {

    /**
     * 返回实体数据类型
     *
     * @return 实体 java 类型
     */
    Class<R> getEntityType();
}
