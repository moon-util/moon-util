package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface TableDeleteWhere<R, TB extends Table<R, TB>> extends WhereClause<TableDeleteWhere<R, TB>>, Done {}
