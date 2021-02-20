package com.moon.accessor.meta;

/**
 * @author benshaoye
 */
public interface Table<R, TB extends Table<R, TB>> extends Commentable{

    /**
     * 返回实体数据类型
     *
     * @return 实体 java 类型
     */
    Class<R> getEntityClass();

    /**
     * 返回实体数据量格式化类名
     *
     * @return 实体完整类名
     */
    default String getEntityClassname() { return getEntityClass().getCanonicalName(); }

    /**
     * 返回数据库表名
     *
     * @return 表名
     */
    String getTableName();

    /**
     * 返回实体对应数据表的列数
     *
     * @return 数据表列数
     */
    int getTableFieldsCount();

    /**
     * 获取所有字段
     *
     * @return 数据表所有字段
     */
    TableField<?, R, TB>[] getTableFields();

    /**
     * 参考：{@inheritDoc}
     *
     * @return 该字段下的第一行注释
     *
     * @see #getComment() 该字段的所有注释
     */
    @Override
    default String getFirstComment() { return getComment(); }

    /**
     * 参考：{@inheritDoc}
     *
     * @return 该字段的文档注释
     *
     * @see #getFirstComment() 该字段的第一行注释
     */
    @Override
    String getComment();
}
