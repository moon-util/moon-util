package com.moon.more.excel.table;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.Renderer;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;
import java.util.List;

import static com.moon.more.excel.table.HeadUtil.*;

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
        // 计算表头行数
        int maxTitleRowCount = calculateHeaderRowCount(columns);
        // 获取所有表头单元格
        List<String>[] tableHead = collectTableHead(columns, maxTitleRowCount);
        // 计算表头合并的单元格
        RegionCell[] merges = collectRegionAddresses(tableHead);

        this.tableHeadCells = tableHead;
        this.merges = merges;
    }

    /**
     * 获取表头行数
     *
     * @return 行数或 0
     */
    int getHeaderRowsCount() {
        Object[] cells = this.tableHeadCells;
        return cells == null ? 0 : cells.length;
    }

    /**
     * 内部方法，获取表头列数
     *
     * @return 如果存在表头，返回表头列数，否则返回 0
     */
    final int getHeaderColsCount() {
        return this.columns.length;
    }

    final void appendTitlesAtRowIdx(List<String> rowTitles, int rowIdx) {
        int rowsCount = getHeaderRowsCount();
        if (rowsCount < 1) {
            int length = getHeaderColsCount();
            for (int i = 0; i < length; i++) {
                rowTitles.add(null);
            }
        } else if (rowIdx < rowsCount) {
            rowTitles.addAll(tableHeadCells[rowIdx]);
        } else {
            int index = rowsCount - 1;
            rowTitles.addAll(tableHeadCells[index]);
        }
    }

    /**
     * 渲染表头
     *
     * @param sheetFactory sheet 渲染器
     */
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

    /**
     * 渲染一条数据
     *
     * @param sheetFactory sheet 渲染器
     * @param iterator     数据集合，
     *                     如果存在第一项（first != null），集合中从第一项开始迭代
     *                     否则从第二项开始迭代
     * @param first        第一项数据（有些数据类型不能获取到第一条数据，所以要单独传第一条数据）
     */
    @Override
    public void renderBody(SheetFactory sheetFactory, Iterator iterator, Object first) {
        IntAccessor indexer = IntAccessor.of();
        if (first != null) {
            indexer.set(0);
            renderRecord(indexer, sheetFactory, first);
        }
        if (iterator != null) {
            while (iterator.hasNext()) {
                indexer.set(0);
                renderRecord(indexer, sheetFactory, iterator.next());
            }
        }
    }

    /**
     * 渲染一条记录
     *
     * @param sheetFactory 如参
     * @param record       集合中的一条数据
     */
    private void renderRecord(IntAccessor indexer, SheetFactory sheetFactory, Object record) {
        RowFactory row = sheetFactory.row();
        doRenderRow(indexer, row, record);
    }

    final void doRenderRow(IntAccessor indexer, RowFactory rowFactory, Object entityData) {
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            columns[i].render(indexer, rowFactory, entityData);
        }
    }
}
