package com.moon.more.excel;

import com.moon.more.excel.RowFactory;

/**
 * @author benshaoye
 */
public interface Evaluator {

    /**
     * 执行一”组“的操作
     * <p>
     * 返回执行的行数
     *
     * @param data
     * @param factory
     *
     * @return
     */
    int evaluate(SheetFactory factory, Object data);
}
