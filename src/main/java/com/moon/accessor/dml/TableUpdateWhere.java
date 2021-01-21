package com.moon.accessor.dml;

import com.moon.accessor.Conditional;
import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface TableUpdateWhere<R, TB extends Table<R, TB>> extends Done {

    TableUpdateWhere<R, TB> and(Conditional conditional);

    TableUpdateWhere<R, TB> or(Conditional conditional);
}
