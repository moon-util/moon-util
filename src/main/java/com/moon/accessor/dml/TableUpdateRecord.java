package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface TableUpdateRecord<R, TB extends Table<R, TB>>
    extends WhereClauseCapable<TableUpdateWhere<R, TB>>, Done {}
