package com.moon.accessor.dialect;

import com.moon.accessor.meta.Table;
import com.moon.accessor.meta.TableField;

import java.sql.Connection;
import java.util.List;

/**
 * @author benshaoye
 */
public interface DialectInserter {

    <R, TB extends Table<R, TB>> int doInsert(
        Connection connection, TB table, TableField<?, R, TB>[] fields, List<Object[]> values
    );
}
