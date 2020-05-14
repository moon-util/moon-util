package com.moon.more.excel.parse;

import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;

/**
 * @author benshaoye
 */
interface MarkRenderer {

    MarkRenderer[] EMPTY = new MarkRenderer[0];

    MarkRenderer NONE = None.NONE;

    enum None implements MarkRenderer {
        /** do nothing */
        NONE;

        @Override
        public void renderRecord(MarkTask c, SheetFactory sf, RowFactory rf, Object d) { }

        @Override
        public void resetAll() { }
    }

    /**
     * 渲染一行数据
     *
     * @param task
     * @param sheetFactory
     * @param factory
     * @param data
     */
    void renderRecord(MarkTask task, SheetFactory sheetFactory, RowFactory factory, Object data);

    /**
     * 重置
     */
    void resetAll();
}
