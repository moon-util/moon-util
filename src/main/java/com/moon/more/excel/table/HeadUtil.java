package com.moon.more.excel.table;

import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author benshaoye
 */
final class HeadUtil {


    static List<String>[] collectTableHead(TableCol[] columns, int maxRowCount) {
        List<String>[] tableHead = new List[maxRowCount];
        for (int i = 0; i < maxRowCount; i++) {
            List<String> titleRow = new ArrayList<>();
            for (TableCol column : columns) {
                column.appendTitlesAtRowIdx(titleRow, i);
            }
            tableHead[i] = titleRow;
        }
        return tableHead;
    }

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

        boolean isTitleEquals(String otherTitle) { return Objects.equals(title, otherTitle); }

        boolean increaseColspanIfTitleEquals(String otherTitle) {
            boolean equals = isTitleEquals(otherTitle);
            if (equals) {
                this.colspan++;
            }
            return equals;
        }

        boolean increaseRowspanIfLikeCell(HeadRegionCell cell) {
            boolean equals = this.colspan == cell.colspan && isTitleEquals(cell.title);
            if (equals) {
                this.rowspan++;
            }
            return equals;
        }

        public int getColIdx() { return colIdx; }

        RegionCell toRegionCell() {
            return new RegionCell(rowIdx, colIdx, rowspan, colspan);
        }
    }
}


