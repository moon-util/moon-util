package com.moon.more.excel;

/**
 * @author benshaoye
 */
public interface Evaluator {

    /**
     * get value
     *
     * @param data
     *
     * @return
     */
    Object getPropertyValue(Object data);

    /**
     * set cell value
     *
     * @param factory
     * @param value
     */
    void setCellValue(CellFactory factory, Object value);

    /**
     * 执行一“组”的操作
     * <p>
     * 返回执行的行数
     *
     * @param data
     * @param factory
     *
     * @return
     */
    void eval(CellFactory factory, Object data);
}
