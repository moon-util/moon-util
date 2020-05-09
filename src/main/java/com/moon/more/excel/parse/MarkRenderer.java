package com.moon.more.excel.parse;

import com.moon.more.excel.Renderer;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;

import java.util.Iterator;

/**
 * @author benshaoye
 */
interface MarkRenderer extends Renderer {

    MarkRenderer[] EMPTY = new MarkRenderer[0];

    /**
     * 渲染表头
     *
     * @param sheetFactory sheet 渲染器
     */
    @Override
    void renderHead(SheetFactory sheetFactory);

    /**
     * 渲染一条数据
     *
     * @param sheetFactory sheet 渲染器
     * @param iterator     数据集合，如果存在第一项（first != null），集合中则只包含除第一项外的剩余项
     * @param first        第一项数据
     */
    @Override
    void renderBody(SheetFactory sheetFactory, Iterator iterator, Object first);

    /**
     * 渲染一行数据
     *
     * @param container
     * @param factory
     * @param data
     */
    void renderRecord(MarkContainer container, SheetFactory sheetFactory, RowFactory factory, Object data);

    /**
     * 重置
     */
    void resetAll();
}
