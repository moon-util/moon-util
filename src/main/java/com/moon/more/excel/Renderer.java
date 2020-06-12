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
     * @param iterator     数据集合，
     *                     如果存在第一项（first != null），集合中从第一项开始迭代
     *                     否则从第二项开始迭代
     * @param first        第一项数据（有些数据类型不能获取到第一条数据，所以要单独传第一条数据）
     */
    @SuppressWarnings("rawtypes")
    void renderBody(SheetFactory sheetFactory, Iterator iterator, Object first);
}
