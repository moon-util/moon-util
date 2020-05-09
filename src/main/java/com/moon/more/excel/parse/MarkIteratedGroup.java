package com.moon.more.excel.parse;

import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;

import java.util.Iterator;
import java.util.List;

import static com.moon.core.util.CollectUtil.toArrayOfEmpty;

/**
 * @author benshaoye
 */
public class MarkIteratedGroup implements MarkRenderer {

    private final MarkRenderer[] columns;
    private final MarkIterated iterateAt;
    private final MarkColumn rootIndexer;

    public MarkIteratedGroup(
        List<MarkIterated> columns, MarkIterated iterateAt, MarkColumn rootIndexer, DetailRoot root, boolean indexed
    ) {
        this.columns = toArrayOfEmpty(columns, MarkIterated[]::new, EMPTY);
        this.rootIndexer = rootIndexer;
        this.iterateAt = iterateAt;
    }

    @Override
    public void renderHead(SheetFactory sheetFactory) {
        // todo 列表头部渲染，供外部调用，不在内部递归调用
    }

    @Override
    public void renderBody(SheetFactory sheetFactory, Iterator iterator, Object first) {
        resetAll();
        if (first != null) {
            renderRecord(MarkContainer.DEFAULT, sheetFactory, sheetFactory.row(), first);
        }
        while (iterator.hasNext()) {
            renderRecord(MarkContainer.DEFAULT, sheetFactory, sheetFactory.row(), iterator.next());
        }
    }

    @Override
    public void renderRecord(MarkContainer container, SheetFactory sheetFactory, RowFactory factory, Object data) {
        MarkIterated iterateAt = getIterateAt();
        // iterateAt.getEvaluator().getPropertyValue(data);
        renderRootCol(container, sheetFactory, factory);
        renderColumn(container, sheetFactory, factory, data);
    }

    private void renderColumn(MarkContainer container, SheetFactory sheetFactory, RowFactory factory, Object data) {
        for (MarkRenderer column : getColumns()) {
            column.renderRecord(container, sheetFactory, factory, data);
        }
    }

    private void renderRootCol(MarkContainer container, SheetFactory sheetFactory, RowFactory factory) {
        MarkColumn root = getRootIndexer();
        if (root != null) {
            root.renderRecord(container, sheetFactory, factory, null);
        }
    }

    @Override
    public void resetAll() {

    }

    /*
     * ~~~~~ getters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    MarkRenderer[] getColumns() { return columns; }

    MarkColumn getRootIndexer() { return rootIndexer; }

    public MarkIterated getIterateAt() { return iterateAt; }
}
