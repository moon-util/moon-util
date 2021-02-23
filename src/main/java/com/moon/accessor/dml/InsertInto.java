package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

import java.util.Collection;

/**
 * @author benshaoye
 */
public interface InsertInto<R, TB extends Table<R, TB>> {

    /**
     * 按实体数据插入
     *
     * @param record 实体数据
     *
     * @return 当前对象
     */
    InsertIntoValues<R, TB> valuesRecord(R record);

    /**
     * 按实体数据插入
     *
     * @param record1 实体数据
     * @param record2 实体数据
     * @param records 实体数据
     *
     * @return this object
     */
    InsertIntoValues<R, TB> valuesRecord(R record1, R record2, R... records);

    /**
     * 按实体数据插入
     *
     * @param record 实体数据
     *
     * @return this object
     */
    InsertIntoValues<R, TB> valuesRecord(Collection<? extends R> record);
}