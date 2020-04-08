package com.moon.more.excel;

import com.moon.core.lang.StringUtil;
import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
class WorkbookProxy {

    private final static boolean DEFAULT_APPEND_DATA = true;

    private final static Object PLACEHOLDER = new byte[0];

    private final Map<Sheet, Table<Integer, Integer, Object>> sheetMap = new HashMap<>();

    private final Workbook workbook;
    private Map<String, StyleBuilder> styleBuilderMap;
    private Map<String, CellStyle> styleSheetDefined;
    private LinkedHashMap<StyleSetter, String> styleSettersMap;

    private Table<Integer, Integer, Object> table;
    private Sheet sheet;
    private int indexOfRow;

    private Row row;
    private int indexOfCell;

    private Map<Integer, Object> rowFilled;
    private Cell cell;

    WorkbookProxy(Workbook workbook) { this.workbook = workbook; }

    Workbook getWorkbook() { return workbook; }

    private Table<Integer, Integer, Object> getTable(Sheet sheet) {
        Table t = this.table;
        if (t == null) {
            t = sheetMap.get(sheet);
            if (t == null) {
                t = new TableImpl();
                sheetMap.put(sheet, t);
            }
            this.table = t;
        }
        return t;
    }

    private Map<Integer, Object> getRowFilled(Table table, int rowIdx) {
        Map<Integer, Object> rowFilled = table.get(rowIdx);
        if (rowFilled == null) {
            rowFilled = new HashMap<>();
            table.put(rowIdx, rowFilled);
        }
        return rowFilled;
    }

    private Map<Integer, Object> getRowFilled(int rowIdx) { return getRowFilled(getTable(sheet), rowIdx); }

    private Map<Integer, Object> getRowFilled() {
        Map<Integer, Object> rowFilled = this.rowFilled;
        if (rowFilled == null) {
            int rowIdx = this.indexOfRow;
            Table table = getTable(sheet);
            rowFilled = table.get(rowIdx);
            if (rowFilled == null) {
                rowFilled = new HashMap<>();
                table.put(rowIdx, rowFilled);
            }
            this.rowFilled = rowFilled;
        }
        return rowFilled;
    }

    /*
     * style
     */

    void definitionStyle(StyleBuilder builder) {
        getStyleSheetBuilderMap().put(builder.getClassname(), builder);
    }

    Map<String, StyleBuilder> getStyleSheetBuilderMap() {
        Map map = this.styleBuilderMap;
        if (map == null) {
            map = new HashMap(8);
            this.styleBuilderMap = map;
        }
        return map;
    }

    Map<String, CellStyle> getStyleSheetDefined() {
        Map map = this.styleSheetDefined;
        if (map == null) {
            map = new HashMap(8);
            this.styleSheetDefined = map;
        }
        return map;
    }

    Map<String, StyleBuilder> obtainStyleSheetMap() {
        return this.styleBuilderMap;
    }

    Map<String, CellStyle> obtainStyleSheetDefined() {
        return this.styleSheetDefined;
    }

    LinkedHashMap<StyleSetter, String> getStyleSettersSet() {
        LinkedHashMap map = this.styleSettersMap;
        if (map == null) {
            map = new LinkedHashMap(8);
            this.styleSettersMap = map;
        }
        return map;
    }

    LinkedHashMap<StyleSetter, String> exchangeStyleSettersSet() {
        LinkedHashMap map = this.styleSettersMap;
        if (isEmpty(map)) {
            return null;
        }
        map = new LinkedHashMap(map);
        this.styleBuilderMap = null;
        return map;
    }

    void addSetter(StyleSetter setter, String classname) {
        LinkedHashMap<StyleSetter, String> settersMap = getStyleSettersSet();
        settersMap.put(setter, classname);
    }

    void applyCellStyle() {
        Map<String, CellStyle> defined = obtainStyleSheetDefined();
        Map<String, StyleBuilder> builderMap = obtainStyleSheetMap();
        if (isEmpty(defined) && isEmpty(builderMap)) {
            return;
        }
        LinkedHashMap<StyleSetter, String> setters = exchangeStyleSettersSet();
        if (isEmpty(setters)) {
            return;
        }
        CellStyle style;
        StyleBuilder builder;
        Workbook workbook = this.workbook;
        for (Map.Entry<StyleSetter, String> entry : setters.entrySet()) {
            StyleSetter setter = entry.getKey();
            String classname = entry.getValue();

            if (builderMap == null) {
                style = defined.get(classname);
            } else {
                if (defined == null) {
                    defined = getStyleSheetDefined();
                }
                builder = builderMap.remove(classname);
                if (builder == null) {
                    style = defined.get(classname);
                } else {
                    style = builder.build(workbook);
                    defined.put(classname, style);
                }
            }
            if (style != null) {
                setter.useStyle(style);
            }
        }
    }

    private static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    /*
     sheet
     */

    Sheet setSheet(Sheet sheet, int startOfRowIndex) {
        this.indexOfRow = startOfRowIndex;
        this.table = null;
        return this.sheet = sheet;
    }

    Sheet setSheet(Sheet sheet, boolean appendRow) {
        return appendRow ? setSheet(sheet, sheet.getLastRowNum()) : setSheet(sheet, 0);
    }

    Sheet getSheet() { return sheet; }

    Sheet useSheet(String sheetName, boolean appendRow) {
        if (StringUtil.isEmpty(sheetName)) {
            return setSheet(workbook.createSheet(), appendRow);
        }
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return setSheet(workbook.createSheet(sheetName), appendRow);
        } else {
            return setSheet(sheet, appendRow);
        }
    }

    Sheet useSheet(int index, boolean appendRow) {
        if (index < workbook.getNumberOfSheets()) {
            return setSheet(workbook.getSheetAt(index), appendRow);
        } else {
            return setSheet(workbook.createSheet(), appendRow);
        }
    }

    Sheet useSheet() { return useSheet(null); }

    Sheet useSheet(String sheetName) {
        return useSheet(sheetName, DEFAULT_APPEND_DATA);
    }

    Sheet useSheet(int index) {
        return useSheet(index, DEFAULT_APPEND_DATA);
    }

    /*
     row
     */

    int getIndexOfRow() { return indexOfRow; }

    int nextIndexOfRow() { return indexOfRow++; }

    int nextIndexOfRow(int skip) {
        int index = indexOfRow + skip;
        this.indexOfRow = index + 1;
        return index;
    }

    Row setRow(Row row, int index) {
        this.indexOfCell = index;
        this.rowFilled = null;
        return this.row = row;
    }

    Row setRow(Row row, boolean appendCell) {
        int index = appendCell ? row.getLastCellNum() + 1 : 0;
        return setRow(row, index);
    }

    Row setRow(Row row) { return setRow(row, DEFAULT_APPEND_DATA); }

    Row getRow() { return row; }

    Row createRow(int index, boolean appendCell) { return setRow(sheet.createRow(index), appendCell); }

    Row createRow(int index) { return createRow(index, DEFAULT_APPEND_DATA); }

    Row useRow(int index, boolean appendCell) { return setRow(sheet.getRow(index), appendCell); }

    Row useRow(int index) { return useRow(index, DEFAULT_APPEND_DATA); }

    Row useOrCreateRow(int index, boolean appendCell) {
        Row row = sheet.getRow(index);
        if (row == null) {
            row = sheet.createRow(index);
        }
        return setRow(row, appendCell);
    }

    Row useOrCreateRow(int index) { return useOrCreateRow(index, DEFAULT_APPEND_DATA); }

    Row nextRow(boolean appendCell) { return createRow(nextIndexOfRow(), appendCell); }

    Row nextRow(int skip, boolean appendCell) { return createRow(nextIndexOfRow(skip), appendCell); }

    Row nextRow() { return nextRow(DEFAULT_APPEND_DATA); }

    Row nextRow(int skip) { return nextRow(skip, DEFAULT_APPEND_DATA); }

    /*
     cell
     */

    int nextIndexOfCell(int skip, int rowspan, int colspan) {
        int cIdx = this.indexOfCell;
        Map<Integer, Object> rowFilled = getRowFilled();
        for (; rowFilled.get(cIdx) != null; cIdx++) { }
        int nCellIdx = cIdx + skip;
        int eCellIdx = nCellIdx + colspan;
        Object placeholder = PLACEHOLDER;
        for (int i = nCellIdx; i < eCellIdx; i++) {
            rowFilled.put(i, placeholder);
        }
        int rowIdx = this.indexOfRow;
        if (rowspan > 1) {
            int nRowIdx = rowIdx + 1;
            int eRowIdx = nRowIdx + (rowspan - 1);
            Table table = getTable(sheet);
            for (int i = nRowIdx; i < eRowIdx; i++) {
                Map<Integer, Object> idxRowFilled = getRowFilled(table, i);
                for (int j = nCellIdx; j < eCellIdx; j++) {
                    idxRowFilled.put(j, placeholder);
                }
            }
        }
        if (rowspan > 1 || colspan > 1) {
            int firstRow = rowIdx - 1;
            int lastRow = firstRow + rowspan - 1;
            sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, nCellIdx, eCellIdx - 1));
        }
        this.indexOfCell = eCellIdx;
        return nCellIdx;
    }

    Cell setCell(Cell cell) { return this.cell = cell; }

    Cell getCell() { return cell; }

    Cell useCell(int index) { return setCell(row.getCell(index)); }

    Cell createCell(int index) { return setCell(row.createCell(index)); }

    Cell nextCell() { return nextCell(0); }

    Cell nextCell(int skip) { return nextCell(skip, 1, 1); }

    Cell nextCell(int rowspan, int colspan) { return nextCell(0, rowspan, colspan); }

    Cell nextCell(int skip, int rowspan, int colspan) {
        return createCell(nextIndexOfCell(skip, rowspan, colspan));
    }
}
