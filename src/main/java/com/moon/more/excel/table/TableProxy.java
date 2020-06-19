package com.moon.more.excel.table;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.CellFactory;
import com.moon.more.excel.PropertyControl;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;

/**
 * @author benshaoye
 */
final class TableProxy {

    private final SheetFactory sheetFactory;
    private final IntAccessor indexer;

    private RowFactory rowFactory;

    private DataNode node;

    TableProxy(SheetFactory sheetFactory) {
        this.sheetFactory = sheetFactory;
        this.indexer = IntAccessor.of();
        this.node = new DataNode(null, null);
    }

    public void setRowData(Object rowData) {
        this.node.data = rowData;
        this.indexer.set(0);
    }

    public Object getRowData() { return node.data; }

    public void nextRow() {
        rowFactory = sheetFactory.row();
    }

    boolean isSkipped() {
        return node.isSkipped();
    }

    void startLocalDataNode(PropertyControl controller) {
        Object thisData = getThisData(controller);
        this.node = new DataNode(this.node, thisData);
    }

    void closeLocalDataNode() {
        this.node = this.node.prev;
    }

    Object getThisData(PropertyControl controller) {
        return controller.control(node.data);
    }

    CellFactory indexedCell(int offset, boolean fillSkipped) {
        doOffsetCells(offset, fillSkipped);
        return rowFactory.index(indexer.getAndIncrement());
    }

    void skip(int offset, boolean fillSkipped) {
        if (fillSkipped) {
            indexedCell(offset, true);
        } else {
            indexer.increment(offset + 1);
        }
    }

    private void doOffsetCells(int offset, boolean fillSkipped) {
        if (fillSkipped) {
            RowFactory factory = this.rowFactory;
            int start = indexer.get();
            for (int i = 0; i < offset; i++) {
                factory.index(i + start);
            }
        }
        indexer.increment(offset);
    }

    private final static class DataNode {

        private final DataNode prev;

        private Object data;

        private DataNode(DataNode prev, Object data) {
            this.prev = prev;
            this.data = data;
        }

        boolean isSkipped() {
            return data == null;
        }
    }
}
