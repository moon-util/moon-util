package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface TableUpdateSetter<R, TB extends Table<R, TB>>
    extends TableUpdateSets<R, TB>, WhereClauseCapable<TableUpdateWhere<R, TB>>, Done {}
