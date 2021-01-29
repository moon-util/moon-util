package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface TableUpdateWhere<R, TB extends Table<R, TB>> extends WhereClause<TableUpdateWhere<R, TB>>, Done {}
