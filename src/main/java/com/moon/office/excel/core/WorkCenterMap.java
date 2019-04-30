package com.moon.office.excel.core;

import com.moon.core.lang.StringUtil;
import com.moon.office.excel.enums.ValueType;
import com.moon.core.util.runner.RunnerDataMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
class WorkCenterMap extends RunnerDataMap
    implements Supplier<Workbook> {
    private final static Class TEMP = WorkCenterMap.class;
    private final Workbook workbook;
    private final int lastRowIndex;
    private final HashMap<Integer, HashMap<Integer, Class>> mergeManage = new HashMap<>();

    public WorkCenterMap(Workbook workbook, Object... data) {
        super(data);
        this.workbook = workbook;
        this.lastRowIndex = (2 << (workbook instanceof HSSFWorkbook ? 16 : 20)) - 5;
    }

    public WorkCenterMap(Supplier<Workbook> type) {
        this(type.get());
    }

    @Override
    public Workbook get() {
        return workbook;
    }

    /*
     * -------------------------------------------------------
     * excel 创建相关
     * -------------------------------------------------------
     */

    private Map<String, TableStyle> styleMaps;

    public void setStyleMaps(Map<String, TableStyle> styleMaps) {
        this.styleMaps = styleMaps;
    }

    private Sheet currentSheet;
    private Row currentRow;
    private Cell currentCell;

    private int currentRowIndex;
    private int currentCellIndex;

    WorkCenterMap createSheet(String sheetName) {
        currentSheet = ensureCreateSheet(String.valueOf(sheetName));
        currentRowIndex = 0;
        mergeManage.clear();
        return this;
    }

    private int prevRowIndex;

    private Sheet ensureCreateSheet(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return workbook.createSheet(sheetName);
        } else {
            String currentName = null;
            int index = sheetName.lastIndexOf('(');
            if (index > 0) {
                int endIndex = sheetName.indexOf(')', index);
                if (endIndex - index > 1) {
                    String wrapped = sheetName.substring(index + 1, endIndex);
                    try {
                        int prevNum = Integer.parseInt(wrapped);
                        currentName = sheetName.substring(0, index) + '(' + (prevNum + 1) + ')';
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
            return ensureCreateSheet(
                currentName == null
                    ? sheetName + "(1)"
                    : currentName
            );
        }
    }

    WorkCenterMap createNextRow(int skips) {
        int actualIndex = prevRowIndex = currentRowIndex + skips, index;
        if (actualIndex > lastRowIndex) {
            createSheet(currentSheet.getSheetName());
            return createNextRow(0);
        } else {
            for (index = currentRowIndex - 1; index < actualIndex; index++) {
                mergeManage.remove(index);
            }

            currentRow = currentSheet.createRow(actualIndex);
            currentRowIndex = actualIndex + 1;

            currentCellIndex = 0;
            return this;
        }
    }

    WorkCenterMap setHeight(short height) {
        if (height > -1) {
            currentRow.setHeight(height);
        }
        return this;
    }

    public void setRowStyle(String className) {
        CellStyleUtil.setRowStyle(workbook, currentSheet, null, currentRow, getStyles(className));
    }

    WorkCenterMap createNextCell(int colspan, int rowspan, int skips, ValueType type) {
        int index = currentCellIndex + skips;
        HashMap<Integer, Class> row = mergeManage.get(prevRowIndex);
        if (row != null) {
            for (; row.get(index) != null; index++) {
            }
        }
        Cell cell = this.currentCell = currentRow.createCell(index);
        cell.setCellType(type.TYPE);
        createMergeRegion(colspan, rowspan, index);
        return this;
    }

    WorkCenterMap setWidth(int width) {
        if (width > -1) {
            currentSheet.setColumnWidth(currentCellIndex - 1, width);
        }
        return this;
    }

    private CellRangeAddress currentRange;

    private void createMergeRegion(int colspan, int rowspan, int index) {
        currentRange = null;
        boolean test = (colspan > 1 && rowspan > 0) || (rowspan > 1 && colspan > 0);
        if (test) {
            int lastCell = index + colspan;
            int lastRow = currentRowIndex + rowspan;
            fillRegionCell(currentRowIndex - 1, lastRow - 2, index, lastCell - 1);
            currentCellIndex = lastCell;
        } else {
            currentCellIndex = index + 1;
        }
    }

    private void fillRegionCell(int firstRow, int lastRow, int firstCell, int lastCell) {
        CellRangeAddress range = new CellRangeAddress(firstRow, lastRow, firstCell, lastCell);
        currentSheet.addMergedRegion(range);
        currentRange = range;
        for (int i = firstRow, outerEnd = lastRow + 1; i < outerEnd; i++) {
            HashMap<Integer, Class> current = mergeManage.get(i);
            if (current == null) {
                mergeManage.put(i, current = new HashMap<>());
            }
            for (int j = firstCell, innerEnd = lastCell + 1; j < innerEnd; j++) {
                current.put(j, TEMP);
            }
        }
    }

    WorkCenterMap setCellValue(Object value) {
        currentCell.setCellValue(String.valueOf(value));
        return this;
    }

    private Map<String, TableStyle[]> styleMapper;

    public Map<String, TableStyle[]> getStyleMapper() {
        if (styleMapper == null) {
            styleMapper = new HashMap<>(16);
        }
        return styleMapper;
    }

    public void setCellStyle(String className) {
        CellStyleUtil.setCellStyle(workbook, currentSheet, currentRange, currentCell, getStyles(className));
    }

    private final static TableStyle[] DEFAULT_STYLES = new TableStyle[0];

    private TableStyle[] getStyles(String className) {
        Map<String, TableStyle[]> mapper = getStyleMapper();
        TableStyle[] styles = mapper.get(className);
        if (styles == null) {
            if (styleMaps == null) {
                return DEFAULT_STYLES;
            }
            String[] names = StringUtil.onlyWhitespace(className.trim()).split(" ");
            int i = 0, length = names.length;
            styles = new TableStyle[length];
            for (; i < length; i++) {
                styles[i] = styleMaps.get(names[i]);
            }
            mapper.put(className, styles);
        }
        return styles;
    }
}
