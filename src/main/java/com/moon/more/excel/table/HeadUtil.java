package com.moon.more.excel.table;

import com.moon.core.util.ListUtil;
import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    static int calculateHeaderRowCount(TableCol[] columns) {
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
     * @param maxRowCount {@link #calculateHeaderRowCount(TableCol[])}
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

    /**
     * 计算合并的单元格信息，返回值中每个{@link RegionCell}的信息至少合并两个单元格
     *
     * @param tableHead {@link #collectTableHead(TableCol[], int)}
     *
     * @return 计算后的合并单元格信息
     *
     * @see TableRenderer
     */
    static RegionCell[] collectRegionAddresses(List<String>[] tableHead) {
        int maxLength = tableHead.length;
        Table<Integer, Integer, HeadRegionCell> table = TableImpl.newLinkedHashTable();
        for (int rowIdx = 0; rowIdx < maxLength; rowIdx++) {
            List<String> rowTitles = tableHead[rowIdx];

            HeadRegionCell cell = null;
            for (int colIdx = 0; colIdx < rowTitles.size(); colIdx++) {
                String thisColTitle = rowTitles.get(colIdx);
                if (cell == null) {
                    cell = new HeadRegionCell(thisColTitle, rowIdx, colIdx);
                } else if (!cell.increaseColspanIfTitleEquals(thisColTitle)) {
                    mergeOrPutTableHeadCell(table, rowIdx, cell);
                    cell = new HeadRegionCell(thisColTitle, rowIdx, colIdx);
                }
            }
            if (cell != null) {
                mergeOrPutTableHeadCell(table, rowIdx, cell);
            }
        }

        List<RegionCell> list = new ArrayList<>();
        table.forEach((x, y, cell) -> {
            if (!cell.isSingleCell()) {
                list.add(cell.toRegionCell());
            }
        });

        return list.toArray(new RegionCell[list.size()]);
    }

    static RegionCell[] collectRegionAddressByCell(List<HeadCell>[] tableHeadCell) {
        List<String>[] result = new List[tableHeadCell.length];
        for (int i = 0; i < tableHeadCell.length; i++) {
            result[i] = tableHeadCell[i].stream().map(HeadCell::getTitle).collect(Collectors.toList());
        }
        return collectRegionAddresses(result);
    }

    private static void mergeOrPutTableHeadCell(
        Table<Integer, Integer, HeadRegionCell> table, int rowIdx, HeadRegionCell cell
    ) {
        int expectColIdx = cell.getColIdx();
        boolean mergedRows = false;
        for (int prevRowIdx = rowIdx - 1; prevRowIdx >= 0; ) {
            HeadRegionCell prevRowDef = table.get(prevRowIdx, expectColIdx);
            if (prevRowDef == null) {
                prevRowIdx--;
            } else {
                mergedRows = prevRowDef.increaseRowspanIfLikeCell(cell);
                break;
            }
        }
        if (!mergedRows) {
            table.put(rowIdx, expectColIdx, cell);
        }
    }

    private final static class HeadRegionCell {

        private final String title;
        private final int rowIdx;
        private final int colIdx;
        private int rowspan;
        private int colspan;

        HeadRegionCell(String title, int rowIdx, int colIdx) {
            this.title = title;
            this.rowIdx = rowIdx;
            this.colIdx = colIdx;
            this.rowspan = 1;
            this.colspan = 1;
        }

        boolean isSingleCell() { return rowspan == 1 && colspan == 1; }

        boolean isTitleEquals(String otherTitle) {
            if (title != null && otherTitle != null) {
                return title.equals(otherTitle);
            }
            return false;
        }

        /**
         * 如果两个单元格相似（标题一致），则合并
         *
         * @param otherTitle 期望标题
         *
         * @return 是否合并完成
         */
        boolean increaseColspanIfTitleEquals(String otherTitle) {
            boolean equals = isTitleEquals(otherTitle);
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
        boolean increaseRowspanIfLikeCell(HeadRegionCell cell) {
            boolean equals = this.colspan == cell.colspan && isTitleEquals(cell.title);
            if (equals) {
                this.rowspan++;
            }
            return equals;
        }

        public int getColIdx() { return colIdx; }

        RegionCell toRegionCell() { return new RegionCell(rowIdx, colIdx, rowspan, colspan); }
    }
}


