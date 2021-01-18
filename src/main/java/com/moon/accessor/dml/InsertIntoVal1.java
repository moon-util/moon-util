package com.moon.accessor.dml;

import com.moon.accessor.meta.Table;

/**
 * @author benshaoye
 */
public interface InsertIntoVal1<T1, R, TB extends Table<R, TB>> extends InsertIntoCol1<T1, R, TB> {

    /**
     * 执行并完成 insert 数据
     *
     * @return 受影响数据数目
     */
    int done();
}
