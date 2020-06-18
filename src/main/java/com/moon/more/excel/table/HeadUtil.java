package com.moon.more.excel.table;

import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.moon.more.excel.table.HeadCell.getTitleOfNull;

/**
 * @author benshaoye
 */
final class HeadUtil {

    /**
     * 计算表头最大行数
     *
     * @param columns {@link ParserUtil#mapAttrs(Map, Function)}
     *
     * @return 行数
     */
    static int maxHeaderRowNum(TableCol[] columns) {
        int maxTitleRowCount = 0;
        for (TableCol column : columns) {
            maxTitleRowCount = Math.max(column.getHeaderRowsCount(), maxTitleRowCount);
        }
        return maxTitleRowCount;
    }

    /**
     * 计算表头，结果呈一个矩阵，可能包含相邻位置值相同的情况
     *
     * @param columns     {@link ParserUtil#mapAttrs(Map, Function)}
     * @param maxRowCount {@link #maxHeaderRowNum(TableCol[])}
     *
     * @return 表头矩阵
     *
     * @see TableRenderer
     */
    static List<HeadCell>[] collectTableHead(TableCol[] columns, int maxRowCount) {
        List<HeadCell>[] tableHead = new List[maxRowCount];
        for (int i = 0; i < maxRowCount; i++) {
            List<HeadCell> titleRow = new ArrayList<>();
            for (TableCol column : columns) {
                column.appendTitlesAtRowIdx(titleRow, i);
            }
            tableHead[i] = titleRow;
        }
        return tableHead;
    }

    static Integer[] collectColumnsWidth(TableCol[] columns) {
        List<Integer> widthList = new ArrayList<>();
        for (TableCol column : columns) {
            column.appendColumnWidth(widthList);
        }
        int size = widthList.size();
        Integer[] widthArr = new Integer[size];
        for (int i = 0; i < size; i++) {
            Integer width = widthList.get(i);
            widthArr[i] = width == null || width < 0 ? null : width;
        }
        return widthArr;
    }

    static List<HeaderCell>[] collectHeaderCells(List<HeadCell>[] collected) {
        Table<Integer, Integer, TableHeaderCell> table = collectRegionAddresses(collected);
        List<HeaderCell>[] headerCells = new List[table.sizeOfRows()];
        for (int i = 0; i < headerCells.length; i++) {
            List<HeaderCell> row = new ArrayList<>();
            Map<Integer, TableHeaderCell> cols = table.get(i);
            cols.forEach((idx, cell) -> row.add(cell));
            headerCells[i] = row;
        }
        return headerCells;
    }

    /**
     * 计算合并的单元格信息，返回值中每个{@link TableCell}的信息至少合并两个单元格
     *
     * @param tableHead {@link #collectTableHead(TableCol[], int)}
     *
     * @return 计算后的合并单元格信息
     *
     * @see TableRenderer
     */
    private static Table collectRegionAddresses(List<HeadCell>[] tableHead) {
        int maxLength = tableHead.length;
        Table<Integer, Integer, TableHeaderCell> table = TableImpl.newLinkedHashTable();
        for (int rowIdx = 0; rowIdx < maxLength; rowIdx++) {
            List<HeadCell> rowTitles = tableHead[rowIdx];

            TableHeaderCell previousCell = null, headerCell;
            for (int colIdx = 0; colIdx < rowTitles.size(); colIdx++) {
                HeadCell thisCell = rowTitles.get(colIdx);
                String thisTitle = getTitleOfNull(thisCell);
                headerCell = new TableHeaderCell(thisCell, rowIdx, colIdx);
                table.put(rowIdx, colIdx, headerCell);
                if (previousCell == null) {
                    previousCell = headerCell;
                } else if (!previousCell.mergeColsIfTitleEquals(thisTitle)) {
                    mergeRowsIfLikeCell(table, previousCell);
                    previousCell = headerCell;
                }
            }
            if (previousCell != null) {
                mergeRowsIfLikeCell(table, previousCell);
            }
        }
        return table;
    }

    private static void mergeRowsIfLikeCell(
        Table<Integer, Integer, TableHeaderCell> table, TableHeaderCell cell
    ) {
        int colIdx = cell.getColIdx();
        int maxRowNum = cell.getRowIdx() + 1;
        for (int rowIdx = 0; rowIdx < maxRowNum; rowIdx++) {
            TableHeaderCell prevRowCell = table.get(rowIdx, colIdx);
            if (prevRowCell != cell && prevRowCell.mergeRowsIfLikeCell(cell)) {
                break;
            }
        }
    }

    private final static class TableHeaderCell extends HeaderCell {

        private final boolean fillSkipped;
        private final String title;
        private final short height;
        private final int rowIdx;
        private final int colIdx;
        private int rowspan;
        private int colspan;

        TableHeaderCell(HeadCell targetCell, int rowIdx, int colIdx) {
            this.title = HeadCell.getTitleOfNull(targetCell);
            this.fillSkipped = targetCell.isOffsetFilled();
            this.height = targetCell.getHeight();
            this.rowIdx = rowIdx;
            this.colIdx = colIdx;
            this.rowspan = 1;
            this.colspan = 1;
        }

        boolean isTitleEquals(String otherTitle) {
            if (title != null && otherTitle != null) {
                return title.equals(otherTitle);
            }
            return false;
        }

        /**
         * 如果两个单元格相似（标题一致），则合并
         *
         * @param thisTitle 期望标题
         *
         * @return 是否合并完成
         */
        boolean mergeColsIfTitleEquals(String thisTitle) {
            boolean equals = isTitleEquals(thisTitle);
            if (equals) {
                this.colspan++;
            }
            return equals;
        }

        /**
         * 如果两个单元格“相似”（标题一致，合并列一致），则合并
         *
         * @param cell 目标单元格
         *
         * @return 是否合并完成
         */
        boolean mergeRowsIfLikeCell(TableCell cell) {
            boolean equals = this.colspan == cell.getColspan() && isTitleEquals(cell.getValue());
            if (equals) {
                this.rowspan++;
            }
            return equals;
        }

        @Override
        public int getColIdx() { return colIdx; }

        @Override
        public String getValue() { return title; }

        @Override
        public int getRowIdx() { return rowIdx; }

        @Override
        public int getRowspan() { return rowspan; }

        @Override
        public int getColspan() { return colspan; }

        @Override
        public short getHeight() { return height; }

        @Override
        final boolean isOffsetCell() { return getValue() == null; }

        @Override
        final boolean isFillSkipped() { return fillSkipped; }
    }
}


