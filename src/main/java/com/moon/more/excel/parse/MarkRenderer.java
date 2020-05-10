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
        public void renderRecord(MarkIteratedExecutor c, SheetFactory sf, RowFactory rf, Object d) { }

        @Override
        public void resetAll() { }
    }

    /**
     * 渲染一行数据
     *
     * @param container
     * @param factory
     * @param data
     */
    void renderRecord(MarkIteratedExecutor container, SheetFactory sheetFactory, RowFactory factory, Object data);

    /**
     * 重置
     */
    void resetAll();
}
