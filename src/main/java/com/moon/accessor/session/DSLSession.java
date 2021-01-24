package com.moon.accessor.session;

import com.moon.accessor.dml.*;
import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * DSL 语法
 *
 * @author benshaoye
 */
@SuppressWarnings("unused")
public interface DSLSession {

    /**
     * 向指定表所有列插入数据
     *
     * @param table 指定表
     * @param <R>   与表关联的实体数据类型
     * @param <TB>  表
     *
     * @return Insert 语句执行器
     */
    <R, TB extends Table<R, TB>> InsertInto<R, TB> insertInto(TB table);

    /**
     * 向指定表指定一列插入数据
     *
     * @param table 指定表
     * @param f1    指定列
     * @param <R>   与表关联的实体数据类型
     * @param <TB>  表
     *
     * @return Insert 语句执行器
     */
    <T1, R, TB extends Table<R, TB>> InsertIntoCol1<T1, R, TB> insertInto(
        TB table, TableField<T1, R, TB> f1
    );

    /**
     * 向指定表指定两列插入数据
     *
     * @param table 指定表
     * @param f1    指定列1
     * @param f2    指定列2
     * @param <R>   与表关联的实体数据类型
     * @param <TB>  表
     *
     * @return Insert 语句执行器
     */
    <T1, T2, R, TB extends Table<R, TB>> InsertIntoCol2<T1, T2, R, TB> insertInto(
        TB table, TableField<T1, R, TB> f1, TableField<T2, R, TB> f2
    );

    /**
     * 查询字段
     *
     * @param fields 字段列表
     *
     * @return 查询
     */
    SelectCols select(TableField<?, ?, ? extends Table<?, ?>>... fields);

    /**
     * 查询单个字段值
     *
     * @param f1   指定字段
     * @param <T1> 字段值的数据类型
     *
     * @return 单列查询
     */
    <T1> SelectCol1<T1> select(TableField<T1, ?, ? extends Table<?, ?>> f1);

    /**
     * 查询多个字段值
     *
     * @param f1   指定字段1
     * @param f2   指定字段2
     * @param <T1> 字段 1 值的数据类型
     * @param <T2> 字段 2 值的数据类型
     *
     * @return 两列查询
     */
    <T1, T2> SelectCol2<T1, T2> select(
        TableField<T1, ?, ? extends Table<?, ?>> f1, TableField<T2, ?, ? extends Table<?, ?>> f2
    );

    /**
     * 更新单表数据
     *
     * @param table 表
     * @param <R>   与表关联的实体数据类型
     * @param <TB>  表
     *
     * @return update
     */
    <R, TB extends Table<R, TB>> TableUpdater<R, TB> update(TB table);

    /**
     * 删除单表数据
     *
     * @param table 表
     * @param <R>   与表关联的实体数据类型
     * @param <TB>  表
     *
     * @return delete
     */
    <R, TB extends Table<R, TB>> TableDeleter<R, TB> delete(TB table);
}
