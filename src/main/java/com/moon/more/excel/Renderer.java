package com.moon.more.excel;

import java.util.Iterator;

/**
 * @author benshaoye
 */
public interface Renderer {

    /**
     * 渲染表头
     *
     * @param sheetFactory sheet 渲染器
     */
    void renderHead(SheetFactory sheetFactory);

    /**
     * 渲染一条数据
     *
     * @param sheetFactory sheet 渲染器
     * @param iterator     数据集合，如果存在第一项（first != null），集合中则不应包含第一项
     * @param first        第一项数据
     */
    void renderBody(SheetFactory sheetFactory, Iterator iterator, Object first);
}
