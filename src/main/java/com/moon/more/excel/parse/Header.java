package com.moon.more.excel.parse;

import com.moon.more.excel.PropertyGetter;

import java.util.*;

/**
 * @author benshaoye
 */
abstract class Header {

    private final static String EMPTY = "";

    public Header() {}

    static class CellDef {

        private final String name;
        private final int rowOffset;
        private final int colOffset;
        private final int rowspan;
        private final int colspan;

        CellDef(String name) { this(name, 0, 0, 1, 1); }

        public CellDef(String name, int rowOffset, int colOffset, int rowspan, int colspan) {
            this.name = name;
            this.rowOffset = rowOffset;
            this.colOffset = colOffset;
            this.rowspan = rowspan;
            this.colspan = colspan;
        }
    }

    private static String emptyOrIdxOf(String[] arr, int index) {
        if (arr == null) { return EMPTY; }
        int len = arr.length;
        // 这里不验证小于 0 的情况了
        return len > index ? arr[index] : EMPTY;
    }

    private static class ColDef {
        String[] columnLabels;
    }

    private static class IfPriorityOrder {

        void transform2FullColumns(ParsedDetail<DefinedGet> getter) {
            List<ColDef> fullColumns = new ArrayList<>();
            List<DefinedGet> columns = new ArrayList<>(getter.getColumns());
            for (DefinedGet column : columns) {
                if (column.isDataFlatten()) {
                    ParsedDetail children = column.getChildrenGroup();

                } else {
                    String[] columnLabels = column.getHeadLabels();
                }
            }
        }
    }

    public void parseHeadIfPriorityOrder(ParsedDetail<DefinedGet> getter) {
        List<List<Object>> definition = new ArrayList<>();
        List<PropertyGetter> colGetters = new ArrayList<>();
        List<DefinedGet> columns = new ArrayList<>(getter.getColumns());
        final int maxRowCnt = getter.getMaxRowsLength();
        columns.sort(CompareOrder.VAL);


        String headCellLabel = getter.hasEnding() ? getter.getStarting().getHeadLabelAsIndexer() : null;
        for (int outerIdx = 0; outerIdx < maxRowCnt; outerIdx++) {
            int rowspan = 1, colspan = 1;
            List rowDefinition = new ArrayList();
            for (int innerIdx = 0; innerIdx < columns.size(); innerIdx++) {
                DefinedGet columnDef = columns.get(innerIdx);
            }
            definition.add(rowDefinition);
        }
    }

    public void parseHeadIfPriorityGroup(ParsedDetail<DefinedGet> getter) {
        List<DefinedGet> columns = getter.getColumns();
    }

    public Map<HeaderCell, Object> doGrouping(List<DefinedGet> columns, int rowOffset) {
        Map<HeaderCell, List<DefinedGet>> grouped = new TreeMap<>();
        int maxLabelsLen = Integer.MIN_VALUE;
        for (DefinedGet column : columns) {
            String[] labels = column.getHeadLabels();
            String name = emptyOrIdxOf(labels, rowOffset);
            HeaderCell cell = new HeaderCell(name, column.getOrder());
            List<DefinedGet> children = grouped.computeIfAbsent(cell, k -> new ArrayList<>());
            children.add(column);
            maxLabelsLen = Math.max(labels.length, maxLabelsLen);
        }
        if (maxLabelsLen > rowOffset - 1) {
            // Map<HeaderCell, Object>=null;
        }
        return null;
    }

    static class HeaderCell implements Comparable<HeaderCell> {

        private final String name;
        private final int order;

        HeaderCell(String name, int order) {
            this.name = name;
            this.order = order;
        }

        @Override
        public int compareTo(HeaderCell o) {
            return CompareGroup.VAL.compare(this, o);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }
            HeaderCell that = (HeaderCell) o;
            return order == that.order && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, order);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("HeaderCell{");
            sb.append("name='").append(name).append('\'');
            sb.append(", order=").append(order);
            sb.append('}');
            return sb.toString();
        }
    }

    enum CompareOrder implements Comparator<DefinedGet> {
        VAL;

        @Override
        public int compare(DefinedGet o1, DefinedGet o2) {
            if (o1 == o2) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            } else {
                int compare = o1.getOrder() - o2.getOrder();
                if (compare == 0) {
                    String[] ls1 = o1.getHeadLabels();
                    String[] ls2 = o2.getHeadLabels();
                    int len1 = ls1.length;
                    int len2 = ls2.length;
                    int len = Math.min(len1, len2);
                    for (int i = 0, res; i < len; i++) {
                        String label1 = ls1[i];
                        String label2 = ls2[i];
                        if ((res = label1.compareTo(label2)) != 0) {
                            return res;
                        }
                    }
                    if (len1 > len2) {
                        return -1;
                    } else if (len2 > len1) {
                        return 1;
                    } else {
                        return o1.getPropertyName().compareTo(o2.getPropertyName());
                    }
                }
            }
            return 0;
        }
    }

    enum CompareGroup implements Comparator<HeaderCell> {
        VAL;

        @Override
        public int compare(HeaderCell o1, HeaderCell o2) {
            if (o1 == o2) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            } else {
                int compare = o1.order - o2.order;
                return compare == 0 ? o1.name.compareTo(o2.name) : compare;
            }
        }
    }
}
