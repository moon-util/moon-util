package com.moon.more.excel.table;

import java.util.List;

/**
 * @author benshaoye
 */
class TableColGroup extends TableCol {

    private final TableRenderer child;

    TableColGroup(Attribute attr, TableRenderer child) {
        super(attr);
        this.child = child;
    }

    @Override
    int getHeaderRowsCount() {
        return super.getHeaderRowsCount() + child.getHeaderRowsCount();
    }

    @Override
    void appendTitlesAtRowIdx(List<String> rowTitles, int rowIdx) {
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
}
