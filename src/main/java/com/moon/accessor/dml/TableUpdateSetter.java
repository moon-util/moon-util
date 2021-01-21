package com.moon.accessor.dml;

import com.moon.accessor.Conditional;
import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface TableUpdateSetter<R, TB extends Table<R, TB>> extends TableUpdater<R, TB>, Done {

    TableUpdateWhere<R, TB> where(Conditional conditional);
}
