package com.moon.poi.excel.table;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.poi.excel.CellWriter;
import com.moon.poi.excel.PropertyControl;
import com.moon.poi.excel.RowWriter;
import com.moon.poi.excel.SheetWriter;

import java.util.Map;

/**
 * @author moonsky
 */
final class TableProxy {

    private final SheetWriter sheetFactory;
    private final IntAccessor indexer;

    private RowWriter rowFactory;

    private DataNode node;

    TableProxy(SheetWriter sheetFactory, Map definitions, int depth) {
        this.sheetFactory = sheetFactory;
        this.indexer = IntAccessor.of();
        DataNode root = new DataNode(null, null, null);
        DataNode head = root, next;
        for (int i = 0; i < depth; i++) {
            next = new DataNode(head, null, null);
            head.next = next;
            head = next;
        }
        this.node = root;
    }

    public void setRowData(Object rowData) {
        this.node.data = rowData;
        this.indexer.set(0);
    }

    public Object getRowData() { return node.data; }

    public void nextRow() { rowFactory = sheetFactory.row(); }

    boolean isSkipped() { return node.isSkipped(); }

    void startLocalDataNode(PropertyControl controller) {
        DataNode next = node.next;
        next.data = getThisData(controller);
        node = next;
    }

    void closeLocalDataNode() { node = node.prev; }

    Object getThisData(PropertyControl controller) {
        return controller.control(node.data);
    }

    CellWriter indexedCell(int offset, boolean fillSkipped) {
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

    final void doOffsetCells(int offset, boolean fillSkipped) {
        if (fillSkipped) {
            RowWriter factory = this.rowFactory;
            int start = indexer.get();
            for (int i = 0; i < offset; i++) {
                factory.index(i + start);
            }
        }
        indexer.increment(offset);
    }

    private final static class DataNode {

        private final DataNode prev;
        private DataNode next;
        private Object data;

        private DataNode(DataNode prev, DataNode next, Object data) {
            this.prev = prev;
            this.next = next;
            this.data = data;
        }

        boolean isSkipped() { return data == null; }
    }
}
