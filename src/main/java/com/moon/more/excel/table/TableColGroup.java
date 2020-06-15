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
    int getHeaderRowsCount() { return super.getHeaderRowsCount() + child.getHeaderRowsCount(); }

    @Override
    void appendTitlesAtRowIdx(List<String> rowTitles, int rowIdx) {
        appendTitles4Offset(rowTitles, rowIdx);
        int superRowsCount = super.getHeaderRowsCount();
        if (rowIdx < superRowsCount) {
            int colsCount = child.getHeaderColsCount();
            for (int i = 0; i < colsCount; i++) {
                super.appendTitlesAtRowIdx(rowTitles, rowIdx);
            }
        } else {
            child.appendTitlesAtRowIdx(rowTitles, rowIdx - superRowsCount);
        }
    }

    @Override
    void render(IntAccessor indexer, RowFactory factory, Object data) {
        // todo render group
        Object entityData = getControl().control(data);
        child.doRenderRow(indexer, factory, entityData);
    }
}
