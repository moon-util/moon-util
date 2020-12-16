package com.moon.poi.excel.table;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.util.MapUtil;
import com.moon.poi.excel.*;
import com.moon.poi.excel.annotation.style.StyleBuilder;
import com.moon.poi.excel.annotation.style.StyleFontBuilder;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author moonsky
 */
final class TableRenderer implements Renderer {

    private final static TableCol[] EMPTY = new TableCol[0];

    private final Map<String, StyleBuilder> definitions;
    private final Class targetClass;
    private final List<HeadCell>[] tableHeadCells;
    private final List<HeaderCell>[] headerCells;
    private final Integer[] columnsWidth;
    private final TableCol[] columns;
    private final int depth;

    TableRenderer(Class targetClass, Map styleMap, TableCol[] columns) {
        this.columns = columns == null ? EMPTY : columns;
        // 计算表头行数
        int maxTitleRowCount = HeadUtil.maxHeaderRowNum(columns);
        // 获取所有表头单元格配置信息
        List<HeadCell>[] tableHead = HeadUtil.collectTableHead(columns, maxTitleRowCount);
        // 列宽
        Integer[] columnsWidth = HeadUtil.collectColumnsWidth(columns);
        // 计算表头合并的单元格
        List<HeaderCell>[] headerCells = HeadUtil.collectHeaderCells(tableHead);

        this.depth = HeadUtil.groupDepth(columns);
        // 收集样式
        Map<String, StyleBuilder> definitionsMap = MapUtil.newHashMap(styleMap);
        for (TableCol column : columns) {
            column.collectStyleMap(definitionsMap);
        }
        this.definitions = MapUtil.immutableIfEmpty(definitionsMap);

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

    final void collectStyleMap(Map<String, StyleBuilder> collector) {
        collector.putAll(definitions);
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

    private void maxHeight(RowWriter factory, List<HeaderCell> cells) {
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
    public void title(SheetWriter factory, TableTitle title) {
        if (title == null || title.getTitle() == null) {
            return;
        }
        int colspan;
        List<HeaderCell>[] cells = this.headerCells;
        if (cells.length > 0) {
            colspan = cells[0].size();
        } else {
            colspan = getHeaderColsCount();
        }

        RowWriter rowFactory = factory.row();
        CellWriter cellFactory = rowFactory.newCell(1, colspan).val(title.getTitle());
        if (title.getClassname() != null) {
            cellFactory.styleAs(title.getClassname());
        }
        if (title.getHeight() > -1) {
            rowFactory.height(title.getHeight());
        }
    }

    /**
     * 渲染表头
     *
     * @param sheetFactory sheet 渲染器
     */
    @Override
    public void renderHead(SheetWriter sheetFactory) {
        Sheet sheet = sheetFactory.getSheet();
        List<HeaderCell>[] headerCells = this.headerCells;
        final int rowIdx = sheetFactory.getRowIndex();
        for (List<HeaderCell> rowCells : headerCells) {
            RowWriter factory = sheetFactory.row();
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
            maxHeight(factory, rowCells);
        }

        // 设置列宽
        sheetFactory.setColumnsWidthBegin(0, this.columnsWidth);
    }

    final void definitionStyles(TableWriter factory) {
        for (Map.Entry<String, StyleBuilder> builderEntry : definitions.entrySet()) {
            final String classname = builderEntry.getKey();
            StyleBuilder builder = builderEntry.getValue();
            if (builder instanceof StyleFontBuilder) {
                BiConsumer<CellStyle, Font> consumer = (BiConsumer<CellStyle, Font>) builder;
                factory.definitionStyle(classname, consumer);
            } else {
                factory.definitionStyle(builderEntry.getKey(), builder);
            }
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
    public void renderBody(SheetWriter sheetFactory, Iterator iterator, Object first) {
        doRenderBody1(sheetFactory, iterator, first);
    }

    private void doRenderBody1(SheetWriter sheetFactory, Iterator iterator, Object first) {
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
        proxy.setRowData(data);
        proxy.nextRow();
        doRenderRow(proxy);
    }

    final void doRenderRow(TableProxy proxy) {
        for (TableCol column : columns) {
            column.render(proxy);
        }
    }

    final void doRenderRow(IntAccessor indexer, RowWriter rowFactory, Object entityData) {
        for (TableCol column : columns) {
            column.render(indexer, rowFactory, entityData);
        }
    }
}
