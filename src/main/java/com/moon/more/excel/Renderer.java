package com.moon.more.excel;

/**
 * @author benshaoye
 */
public interface Renderer<T> {

    /**
     * 渲染表头
     *
     * @param sheetFactory sheet 渲染器
     * @param skipCol      起始位置
     */
    void renderHead(SheetFactory sheetFactory, int skipCol);

    /**
     * 渲染一条数据
     *
     * @param sheetFactory sheet 渲染器
     * @param skipCol      起始位置
     * @param data         数据
     */
    void renderRecord(SheetFactory sheetFactory, int skipCol, T data);
}
