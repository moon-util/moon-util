package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface SelectCol1From<T1, R, TB extends Table<R, TB>> extends SelectCol1Where<T1, R, TB> {

    <TB1 extends Table<?, TB1>> SelectCol1Join<T1, R, TB> leftJoin(TB1 table);

    <TB1 extends Table<?, TB1>> SelectCol1Join<T1, R, TB> rightJoin(TB1 table);

    <TB1 extends Table<?, TB1>> SelectCol1Join<T1, R, TB> innerJoin(TB1 table);

    <TB1 extends Table<?, TB1>> SelectCol1Join<T1, R, TB> join(TB1 table);

    <TB1 extends Table<?, TB1>> SelectCol1Join<T1, R, TB> join(TB1 table, JoinType type);

    SelectCol1Where<T1, R, TB> where();
}
