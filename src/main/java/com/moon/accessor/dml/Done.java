package com.moon.accessor.dml;

/**
 * @author benshaoye
 */
public interface Done {

    /**
     * 完成执行，用于 insert、update、delete 语句
     *
     * @return 执行数目
     */
    int done();
}
