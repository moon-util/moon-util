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

    private final List<HeadCell>[] tableHeadCells;
    private final Integer[] columnsWidth;
    private final RegionCell[] merges;
    private final TableCol[] columns;

    TableRenderer(TableCol[] columns) {
        this.columns = columns == null ? EMPTY : columns;
        // 计算表头行数
        int maxTitleRowCount = calculateHeaderRowCount(columns);
        // 获取所有表头单元格配置信息
        List<HeadCell>[] tableHead = collectTableHead(columns, maxTitleRowCount);

        Integer[] columnsWidth = collectColumnsWidth(columns);
        // 计算表头合并的单元格
        RegionCell[] merges = collectRegionAddressByCell(tableHead);

        this.tableHeadCells = tableHead;
        this.columnsWidth = columnsWidth;
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
    final int getHeaderColsCount() { return this.columns.length; }

    void appendColumnWidth(List<Integer> columnsWidth) {
        Integer[] widthArr = this.columnsWidth;
        for (Integer width : widthArr) {
            columnsWidth.add(width == null ? -1 : width);
        }
    }

    final void appendTitlesAtRowIdx(List<HeadCell> rowTitles, int rowIdx) {
        int rowsCount = getHeaderRowsCount();
        if (rowsCount < 1) {
            int length = getHeaderColsCount();
            for (int i = 0; i < length; i++) {
                rowTitles.add(new HeadCell());
            }
        } else if (rowIdx < rowsCount) {
            rowTitles.addAll(tableHeadCells[rowIdx]);
        } else {
            int index = rowsCount - 1;
            rowTitles.addAll(tableHeadCells[index]);
        }
    }

    private void maxHeight(RowFactory factory, List<HeadCell> cells) {
        short finalHeight = -1;
        if (cells != null) {
            for (HeadCell cell : cells) {
                finalHeight = cell != null && cell.getHeight() > finalHeight ? cell.getHeight() : finalHeight;
            }
        }
        if (finalHeight > -1) {
            factory.height(finalHeight);
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
        List<HeadCell>[] tableHeadRows = this.tableHeadCells;
        for (int i = 0; i < tableHeadRows.length; i++) {
            List<HeadCell> thisRow = tableHeadRows[i];
            RowFactory factory = sheetFactory.row();
            for (HeadCell cell : thisRow) {
                factory.next(cell == null ? null : cell.getTitle());
            }
            maxHeight(factory, thisRow);
        }

        // 合并表头单元格
        Sheet sheet = sheetFactory.getSheet();
        RegionCell[] merges = this.merges;
        for (RegionCell merge : merges) {
            sheet.addMergedRegion(merge.region());
        }

        // 设置表头宽度
        sheetFactory.setColumnsWidthBegin(0, this.columnsWidth);
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
        row.style("header");
    }

    final void doRenderRow(IntAccessor indexer, RowFactory rowFactory, Object entityData) {
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            columns[i].render(indexer, rowFactory, entityData);
        }
    }
}
