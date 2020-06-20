package com.moon.more.excel.table;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.annotation.style.StyleBuilder;

import java.util.List;
import java.util.Map;

/**
 * @author benshaoye
 */
final class TableColGroup extends TableCol {

    private final Class targetClass;
    private final TableRenderer child;

    TableColGroup(AttrConfig config, TableRenderer child) {
        super(config);
        this.child = child;
        this.targetClass = config.getTargetClass();
    }

    @Override
    int getDepth() { return child.getDepth() + 1; }

    @Override
    int getCrossColsCount() { return child.getHeaderColsCount(); }

    @Override
    int getHeaderRowsCount() { return super.getHeaderRowsCount() + child.getHeaderRowsCount(); }

    @Override
    void collectStyleMap(
        Map<Class, Map<String, StyleBuilder>> definitions, Map sourceMap
    ) { child.collectStyleMap(definitions, sourceMap); }

    @Override
    void appendColumnWidth(List<Integer> columnsWidth) {
        child.appendColumnWidth(columnsWidth);
    }

    @Override
    void appendTitlesAtRowIdx(List<HeadCell> rowTitles, int rowIdx) {
        appendTitles4Offset(rowTitles, rowIdx);
        int superRowsLength = super.getHeaderRowsLength();
        if (rowIdx < superRowsLength) {
            int colsCount = child.getHeaderColsCount();
            int count = colsCount;
            for (int i = 0; i < count; i++) {
                super.appendTitlesAtRowIdx(rowTitles, rowIdx);
            }
        } else {
            child.appendTitlesAtRowIdx(rowTitles, rowIdx - superRowsLength);
        }
    }

    @Override
    void render(TableProxy proxy) {
        proxy.startLocalDataNode(getControl(), targetClass);
        child.doRenderRow(proxy);
        proxy.closeLocalDataNode();
    }

    @Override
    void render(IntAccessor indexer, RowFactory factory, Object data) {
        Object entityData = getControl().control(data);
        child.doRenderRow(indexer, factory, entityData);
    }
}
