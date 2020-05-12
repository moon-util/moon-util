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
    default void eval(CellFactory factory, Object data) {
        evalOnOriginal(factory, getPropertyValue(data));
    }

    /**
     * 始终直接设置
     *
     * @param factory
     * @param data
     */
    default void evalOnOriginal(CellFactory factory, Object data) { setCellValue(factory, data); }
}
