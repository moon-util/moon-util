package com.moon.poi.excel;

import java.util.Iterator;

/**
 * @author moonsky
 */
public interface Renderer {

    /**
     * 设置总标题，独占一行，并合并列
     *
     * @param factory sheet 渲染器
     * @param caption 标题
     */
    default void title(SheetFactory factory, String caption) {
        title(factory, caption, null, -1);
    }

    /**
     * 设置总标题，独占一行，并合并列
     *
     * @param factory   sheet 渲染器
     * @param caption   标题
     * @param classname 定义的样式名
     * @param height    标题行高
     */
    void title(SheetFactory factory, String caption, String classname, int height);

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
