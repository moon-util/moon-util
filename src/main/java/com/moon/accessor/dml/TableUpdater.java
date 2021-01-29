package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

/**
 * @author benshaoye
 */
public interface TableUpdater<R, TB extends Table<R, TB>> extends TableUpdateSets<R, TB> {

    /**
     * 更新设置实体所有字段数据（如果有 WHERE 子句的话）
     *
     * @param record 实体
     *
     * @return setter
     */
    TableUpdateRecord<R, TB> setRecord(R record);
}
