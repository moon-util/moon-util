package com.moon.more.excel.table;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.RowFactory;

import java.util.List;

/**
 * @author benshaoye
 */
class TableColGroup extends TableCol {

    private final TableRenderer child;

    TableColGroup(AttrConfig config, TableRenderer child) {
        super(config);
        this.child = child;
    }

    @Override
    int getCrossColsCount() {
        return child.getHeaderColsCount();
    }

    @Override
    int getHeaderRowsCount() { return super.getHeaderRowsCount() + child.getHeaderRowsCount(); }

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
            // int offsetVal = child.getTotalOffsetVal();
            int count = colsCount;
            for (int i = 0; i < count; i++) {
                super.appendTitlesAtRowIdx(rowTitles, rowIdx);
            }
        } else {
            child.appendTitlesAtRowIdx(rowTitles, rowIdx - superRowsLength);
        }
    }

    @Override
    void render(IntAccessor indexer, RowFactory factory, Object data) {
        // todo render group
        Object entityData = getControl().control(data);
        child.doRenderRow(indexer, factory, entityData);
    }
}
