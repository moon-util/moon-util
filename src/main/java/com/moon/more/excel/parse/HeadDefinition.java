package com.moon.more.excel.parse;

import java.util.*;

/**
 * @author benshaoye
 */
public class HeadDefinition {

    public HeadDefinition() {
    }

    static void transformParsedDetail(ParsedDetail<DefinedGet> detail) {
        List<DefinedGet> gets = detail.getColumns();
        for (DefinedGet get : gets) {

        }
    }

    static void transform4Group() {

    }

    static Map<String, Integer> getLabelsOrderMap(List<DefinedGet> gets) {
        Map<String, Integer> orderMap = new HashMap<>();
        for (DefinedGet get : gets) {
            String[] labels = ensureNonNull(get.getHeadLabels());
            for (String label : labels) {
                // orderMap.pu
            }
        }
        return null;
    }

    private final static String[] EMPTY = {};

    protected static String[] ensureNonNull(String[] labels) { return labels == null ? EMPTY : labels; }

    static void doGrouping(List<DefinedGet> gets, String[] topHeadLabels) {
        topHeadLabels = ensureNonNull(topHeadLabels);
        Map<String, HeadCell> labelMap = new HashMap<>(gets.size());
        Map<HeadCell, List<DefinedGet>> tempMap = new HashMap<>(gets.size());
        for (DefinedGet get : gets) {
            String[] labels = get.getHeadLabels();
            String name = get.getName();
            HeadCell cell = labelMap.computeIfAbsent(name, HeadCell::new);
            cell.compareAndSetMin(get.getOrder());
            List<DefinedGet> children = tempMap.computeIfAbsent(cell, k -> new ArrayList<>());
            children.add(get);
        }
        Map<HeadCell,List<DefinedGet>> sortedGroupMap = new TreeMap<>(HeadSortEnum.GROUP);
        sortedGroupMap.putAll(tempMap);

    }

    static class HeadCell implements HeadSortable {

        final String name;
        int order;

        HeadCell(String name) { this(name, Integer.MAX_VALUE); }

        HeadCell(String name, int order) {
            this.name = name;
            this.order = order;
        }

        public void compareAndSetMin(int order) {
            this.order = Math.min(order, this.order);
        }

        @Override
        public String getName() { return name; }

        @Override
        public int getOrder() { return order; }

        @Override
        public boolean equals(Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }
            HeadCell headCell = (HeadCell) o;
            return Objects.equals(name, headCell.name);
        }

        @Override
        public int hashCode() { return Objects.hash(name); }
    }
}
