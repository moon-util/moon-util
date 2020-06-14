package com.moon.more.excel.table;

import com.moon.core.lang.ArrayUtil;
import com.moon.core.util.CollectUtil;
import com.moon.more.excel.Renderer;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;
import java.util.List;

import static com.moon.more.excel.table.HeadUtil.collectRegionAddresses;
import static com.moon.more.excel.table.HeadUtil.collectTableHead;

/**
 * @author benshaoye
 */
final class TableRenderer implements Renderer {

    private final static TableCol[] EMPTY = new TableCol[0];

    private final List<String>[] tableHeadCells;
    private final RegionCell[] merges;
    private final TableCol[] columns;

    TableRenderer(TableCol[] columns) {
        this.columns = columns == null ? EMPTY : columns;

        int maxTitleRowCount = 0;
        for (TableCol column : columns) {
            maxTitleRowCount = Math.max(column.getHeaderRowsCount(), maxTitleRowCount);
        }

        // 获取所有表头单元格
        List<String>[] tableHead = collectTableHead(columns, maxTitleRowCount);
        // 计算所有表头合并单元格
        RegionCell[] merges = collectRegionAddresses(tableHead);

        this.tableHeadCells = tableHead;
        this.merges = merges;
    }

    int getHeaderRowsCount() {
        return tableHeadCells.length;
    }

    int getHeaderColsCount() {
        return ArrayUtil.isEmpty(tableHeadCells) ? 0 : CollectUtil.size(tableHeadCells[0]);
    }

    void appendTitlesAtRowIdx(List<String> rowTitles, int rowIdx) {
        int rowsCount = getHeaderRowsCount();
        if (rowIdx < rowsCount) {
            rowTitles.addAll(tableHeadCells[rowIdx]);
        } else {
            rowTitles.addAll(tableHeadCells[rowsCount - 1]);
        }
    }

    @Override
    public void renderHead(SheetFactory sheetFactory) {
        // 渲染表头
        List<String>[] tableHeadRows = this.tableHeadCells;
        for (List<String> tableHeadRow : tableHeadRows) {
            RowFactory factory = sheetFactory.row();
            for (String title : tableHeadRow) {
                factory.next(title);
            }
        }

        // 合并表头单元格
        Sheet sheet = sheetFactory.getSheet();
        RegionCell[] merges = this.merges;
        for (RegionCell merge : merges) {
            sheet.addMergedRegion(merge.region());
        }
    }

    @Override
    public void renderBody(SheetFactory sheetFactory, Iterator iterator, Object first) {
        TableCol[] columns = this.columns;
        if (first != null) {
            renderRecord(columns, sheetFactory, first);
        }
        if (iterator != null) {
            while (iterator.hasNext()) {
                renderRecord(columns, sheetFactory, iterator.next());
            }
        }
    }

    private void renderRecord(TableCol[] columns, SheetFactory sheetFactory, Object record) {
        RowFactory row = sheetFactory.row();
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            columns[i].render(row.index(i), record);
        }
    }
}
