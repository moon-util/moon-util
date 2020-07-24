package com.moon.poi.excel.table;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.poi.excel.CellFactory;
import com.moon.poi.excel.PropertyControl;
import com.moon.poi.excel.RowFactory;
import com.moon.poi.excel.SheetFactory;
import com.moon.poi.excel.annotation.style.StyleBuilder;

import java.util.Map;

/**
 * @author moonsky
 */
final class TableProxy {

    private final Map<?, Map> definitions;
    private final SheetFactory sheetFactory;
    private final IntAccessor indexer;

    private RowFactory rowFactory;

    private DataNode node;

    TableProxy(SheetFactory sheetFactory, Map definitions, int depth) {
        this.sheetFactory = sheetFactory;
        this.definitions = definitions;
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

    public void setRowData(Object rowData, Class targetClass) {
        this.node.styleMap = StyleUtil.getScopedMapOrEmpty(definitions, targetClass);
        this.node.data = rowData;
        this.indexer.set(0);
    }

    public Object getRowData() { return node.data; }

    public void nextRow() { rowFactory = sheetFactory.row(); }

    boolean isSkipped() { return node.isSkipped(); }

    void startLocalDataNode(PropertyControl controller, Class targetClass) {
        DataNode next = node.next;
        next.styleMap = StyleUtil.getScopedMapOrEmpty(definitions, targetClass);
        next.data = getThisData(controller);
        node = next;
    }

    void closeLocalDataNode() { node = node.prev; }

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

    final void doOffsetCells(int offset, boolean fillSkipped) {
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
        private DataNode next;

        private Map<?, StyleBuilder> styleMap;
        private Object data;

        private DataNode(DataNode prev, DataNode next, Object data) {
            this.prev = prev;
            this.next = next;
            this.data = data;
        }

        boolean isSkipped() { return data == null; }
    }
}
