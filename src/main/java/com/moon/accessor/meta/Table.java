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

    /**
     * 返回实体数据量格式化类名
     *
     * @return 实体完整类名
     */
    default String getEntityClassname() { return getEntityType().getCanonicalName(); }

    /**
     * 返回数据库表名
     *
     * @return 表名
     */
    String getTableName();
}
