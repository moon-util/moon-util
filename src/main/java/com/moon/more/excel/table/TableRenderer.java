package com.moon.more.excel.table;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.CellFactory;
import com.moon.more.excel.Renderer;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;
import com.moon.more.excel.annotation.style.StyleBuilder;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.moon.more.excel.table.HeadUtil.*;

/**
 * @author moonsky
 */
final class TableRenderer implements Renderer {

    private final static TableCol[] EMPTY = new TableCol[0];

    private final Map<Class, Map<String, StyleBuilder>> definitions;
    private final Class targetClass;
    private final List<HeadCell>[] tableHeadCells;
    private final List<HeaderCell>[] headerCells;
    private final Integer[] columnsWidth;
    private final TableCol[] columns;
    private final int depth;

    TableRenderer(Class targetClass, Map styleMap, TableCol[] columns) {
        this.columns = columns == null ? EMPTY : columns;
        // 计算表头行数
        int maxTitleRowCount = maxHeaderRowNum(columns);
        // 获取所有表头单元格配置信息
        List<HeadCell>[] tableHead = collectTableHead(columns, maxTitleRowCount);
        // 列宽
        Integer[] columnsWidth = collectColumnsWidth(columns);
        // 计算表头合并的单元格
        List<HeaderCell>[] headerCells = collectHeaderCells(tableHead);

        this.depth = HeadUtil.groupDepth(columns);
        this.definitions = StyleUtil.collectStyleMap(columns, styleMap);

        this.targetClass = targetClass;
        this.headerCells = headerCells;
        this.tableHeadCells = tableHead;
        this.columnsWidth = columnsWidth;
    }

    public int getDepth() { return depth; }

    public Class getTargetClass() { return targetClass; }

    /**
     * 获取表头行数
     *
     * @return 行数或 0
     */
    int getHeaderRowsCount() {
        Object[] cells = this.headerCells;
        return cells == null ? 0 : cells.length;
    }

    /**
     * 内部方法，获取表头列数
     *
     * @return 如果存在表头，返回表头列数，否则返回 0
     */
    final int getHeaderColsCount() {
        TableCol[] cols = this.columns;
        int columnsCount = 0;
        for (TableCol col : cols) {
            columnsCount += col.getCrossColsCount();
        }
        return columnsCount;
    }

    void collectStyleMap(
        Map<Class, Map<String, StyleBuilder>> definitions, Map sourceMap
    ) {
        Map<Class, Map<String, StyleBuilder>> thisDef = this.definitions;
        for (Map.Entry<Class, Map<String, StyleBuilder>> classMapEntry : thisDef.entrySet()) {
            Map<String, StyleBuilder> builderMap = classMapEntry.getValue();
            Map newMap = new HashMap(sourceMap);
            newMap.putAll(builderMap);
            if (!newMap.isEmpty()) {
                definitions.put(classMapEntry.getKey(), newMap);
            }
        }
    }

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
                rowTitles.add(HeadCell.EMPTY);
            }
        } else if (rowIdx < rowsCount) {
            rowTitles.addAll(tableHeadCells[rowIdx]);
        } else {
            int index = rowsCount - 1;
            rowTitles.addAll(tableHeadCells[index]);
        }
    }

    private short maxHeight(HeaderCell cell, short otherHeight) {
        return cell != null && cell.getHeight() > otherHeight ? cell.getHeight() : otherHeight;
    }

    private void maxHeight(RowFactory factory, List<HeaderCell> cells) {
        short finalHeight = -1;
        if (cells != null) {
            for (HeaderCell cell : cells) {
                finalHeight = maxHeight(cell, finalHeight);
            }
        }
        if (finalHeight > -1) {
            factory.height(finalHeight);
        }
    }

    @Override
    public void captionWith(SheetFactory factory, String caption, String classname, int height) {
        int colspan;
        List<HeaderCell>[] cells = this.headerCells;
        if (cells.length > 0) {
            colspan = cells[0].size();
        } else {
            colspan = getHeaderColsCount();
        }

        RowFactory rowFactory = factory.row();
        CellFactory cellFactory = rowFactory.cell(1, colspan).val(caption);
        if (classname != null) {
            cellFactory.styleAs(classname);
        }
        if (height > -1) {
            rowFactory.height(height);
        }
    }

    /**
     * 渲染表头
     *
     * @param sheetFactory sheet 渲染器
     */
    @Override
    public void renderHead(SheetFactory sheetFactory) {
        Sheet sheet = sheetFactory.getSheet();
        List<HeaderCell>[] headerCells = this.headerCells;
        final int rowIdx = sheetFactory.getRowNum();
        for (List<HeaderCell> rowCells : headerCells) {
            RowFactory factory = sheetFactory.row();
            for (HeaderCell cell : rowCells) {
                // 设置表头标题
                if (cell.isOffsetCell()) {
                    if (cell.isFillSkipped()) {
                        factory.index(cell.getColIdx());
                    }
                } else {
                    factory.index(cell.getColIdx()).val(cell.getValue());
                }
                // 合并表头单元格
                cell.merge(sheet, rowIdx);
            }
            // maxHeight(factory, rowCells);
        }

        // 设置列宽
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
        doRenderBody1(sheetFactory, iterator, first);
    }

    private void doRenderBody1(SheetFactory sheetFactory, Iterator iterator, Object first) {
        TableProxy proxy = new TableProxy(sheetFactory, definitions, depth);
        if (first != null) {
            renderRecord(proxy, first);
        }
        if (iterator != null) {
            while (iterator.hasNext()) {
                renderRecord(proxy, iterator.next());
            }
        }
    }

    private void renderRecord(TableProxy proxy, Object data) {
        proxy.setRowData(data, targetClass);
        proxy.nextRow();
        doRenderRow(proxy);
    }

    final void doRenderRow(TableProxy proxy) {
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            columns[i].render(proxy);
        }
    }

    final void doRenderRow(IntAccessor indexer, RowFactory rowFactory, Object entityData) {
        int length = columns.length;
        for (int i = 0; i < length; i++) {
            columns[i].render(indexer, rowFactory, entityData);
        }
    }
}
