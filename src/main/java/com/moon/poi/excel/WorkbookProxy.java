package com.moon.poi.excel;

import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ref.FinalAccessor;
import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.HashMap;
import java.util.Map;

/**
 * @author moonsky
 */
public final class WorkbookProxy {

    private final static boolean DEFAULT_APPEND_DATA = true;

    private final static Object PLACEHOLDER = new byte[0];

    private final FinalAccessor<CellRangeAddress> mergedOnCell = FinalAccessor.of();

    private final Map<Sheet, Table<Integer, Integer, Object>> sheetMap = new HashMap<>();

    private final WorkbookType type;
    private final Workbook workbook;

    private ProxyStyleModel proxyStyleModel;
    private ProxyCommentModel proxyCommentModel;

    private Table<Integer, Integer, Object> table;
    private Sheet sheet;
    private int indexOfRow;

    private Row row;
    private int indexOfCell;

    private Map<Integer, Object> rowFilled;
    private Cell cell;

    WorkbookProxy(Workbook workbook) {
        this.workbook = workbook;
        if (WorkbookType.XLSX.test(workbook)) {
            type = WorkbookType.XLSX;
        } else if (WorkbookType.XLS.test(workbook)) {
            type = WorkbookType.XLS;
        } else if (WorkbookType.SUPER.test(workbook)) {
            type = WorkbookType.SUPER;
        } else {
            type = null;
        }
    }

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

    private static Map<Integer, Object> getRowFilled(Table table, int rowIdx) {
        Map<Integer, Object> rowFilled = table.get(rowIdx);
        if (rowFilled == null) {
            rowFilled = new HashMap<>();
            table.put(rowIdx, rowFilled);
        }
        return rowFilled;
    }

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

    private static void useProxyModel(ProxyModel proxyModel, Object from) {
        if (proxyModel != null) {
            proxyModel.use(from);
        }
    }

    private ProxyCommentModel getCommentProxy() { return proxyCommentModel; }

    private ProxyStyleModel getStyleProxy() { return this.proxyStyleModel; }

    private ProxyCommentModel ensureCommentProxy() {
        ProxyCommentModel proxy = this.getCommentProxy();
        if (proxy == null) {
            proxy = new ProxyCommentModel();
            this.proxyCommentModel = proxy;
        }
        return proxy;
    }

    private ProxyStyleModel ensureStyleProxy() {
        ProxyStyleModel proxy = this.getStyleProxy();
        if (proxy == null) {
            proxy = new ProxyStyleModel();
            this.proxyStyleModel = proxy;
        }
        return proxy;
    }

    void definitionBuilder(ProxyStyleBuilder builder) { ensureStyleProxy().addBuilder(builder); }

    void addSetter(ProxyStyleSetter setter, String classname) { ensureStyleProxy().addSetter(classname, setter); }

    ProxyStyleBuilder findBuilder(ProxyStyleSetter setter) {
        ProxyStyleModel styleModel = ensureStyleProxy();
        Object classname = styleModel.find(setter);
        return styleModel.find(classname);
    }

    void definitionBuilder(ProxyCommentBuilder builder) { ensureCommentProxy().addBuilder(builder); }

    void addSetter(ProxyCommentSetter setter, String unique) { ensureCommentProxy().addSetter(unique, setter); }

    void removeSetter(ProxyCommentSetter setter) { ensureCommentProxy().removeSetter(setter); }

    void applyProxiedModel() {
        useProxyModel(getStyleProxy(), workbook);
        useProxyModel(getCommentProxy(), null);
    }

    /*
     workbook
     */

    Comment createComment(String content) {
        Comment comment = createComment();
        comment.setString(createRichText(content));
        return comment;
    }

    Comment createComment() {
        Drawing drawing = getSheet().createDrawingPatriarch();
        return drawing.createCellComment(getWorkbookType().newAnchor());
    }

    RichTextString createRichText(String content) { return getWorkbookType().newRichText(content); }

    WorkbookType getWorkbookType() { return type; }

    /*
     sheet
     */

    Sheet setSheet(Sheet sheet, int startOfRowIndex) {
        this.indexOfRow = startOfRowIndex;
        this.table = null;
        this.sheet = sheet;
        return sheet;
    }

    Sheet setSheet(Sheet sheet, boolean appendRow) {
        if (appendRow) {
            int lastRowIdx = sheet.getLastRowNum();
            // return setSheet(sheet, lastRowIdx == 0 ? 0 : lastRowIdx + 1);
            return setSheet(sheet, lastRowIdx);
        }
        return setSheet(sheet, 0);
    }

    Sheet getSheet() { return sheet; }

    Sheet useSheet(String sheetName, boolean appendRow) {
        if (StringUtil.isEmpty(sheetName)) {
            return setSheet(workbook.createSheet(), false);
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

    Sheet useSheet(String sheetName) { return useSheet(sheetName, DEFAULT_APPEND_DATA); }

    /*
     row
     */

    int nextIndexOfRow() { return indexOfRow++; }

    int nextIndexOfRow(int offset) {
        int index = indexOfRow + offset;
        this.indexOfRow = index + 1;
        return index;
    }

    Row setRow(Row row, int index) {
        this.indexOfCell = index;
        this.rowFilled = null;
        return this.row = row;
    }

    Row setRow(Row row, boolean appendCell) {
        int index = appendCell ? row.getLastCellNum() : 0;
        return setRow(row, Math.max(index, 0));
    }

    Row getRow() { return row; }

    int getIndexOfRow() { return indexOfRow; }

    private Row createRow(int index, boolean appendCell) { return setRow(sheet.createRow(index), appendCell); }

    Row useOrCreateRow(int index, boolean appendCell) {
        Row row = sheet.getRow(index);
        if (row == null) {
            row = sheet.createRow(index);
        }
        // 保持 indexOfRow 指向下一行
        this.indexOfRow = index + 1;
        return setRow(row, appendCell);
    }

    Row nextRow(boolean appendCell) { return createRow(nextIndexOfRow(), appendCell); }

    Row nextRow(int offset, boolean appendCell) { return createRow(nextIndexOfRow(offset), appendCell); }

    Row nextRow() { return nextRow(DEFAULT_APPEND_DATA); }

    Row nextRow(int offset) { return nextRow(offset, DEFAULT_APPEND_DATA); }

    /*
     cell
     */

    void writeImageOnCell(){
        Drawing drawing = sheet.createDrawingPatriarch();
        // drawing.createPicture()
    }

    int nextIndexOfCell(int offset, int rowspan, int colspan) {
        int cIdx = this.indexOfCell;
        Map<Integer, Object> rowFilled = getRowFilled();
        for (; rowFilled.get(cIdx) != null; cIdx++) { }
        int nCellIdx = cIdx + offset;
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
        CellRangeAddress region = null;
        if (rowspan > 1 || colspan > 1) {
            int fr = rowIdx - 1;
            int lr = fr + rowspan - 1;
            region = new CellRangeAddress(fr, lr, nCellIdx, eCellIdx - 1);
            sheet.addMergedRegion(region);
        }
        mergedOnCell.replaceOf(region);
        this.indexOfCell = eCellIdx;
        return nCellIdx;
    }

    Cell setCell(Cell cell) { return this.cell = cell; }

    CellRangeAddress getRegion() { return mergedOnCell.get(); }

    private Cell createCell(int index) { return setCell(row.createCell(index)); }

    private void fillIndex(Integer index) {
        getRowFilled().put(index, PLACEHOLDER);
    }

    Cell setCellAndFill(Cell cell, int index) {
        fillIndex(index);
        return setCell(cell);
    }

    Cell createCellAndFill(int index) {
        fillIndex(index);
        return createCell(index);
    }

    Cell useOrCreateCell(int index, boolean alwaysCreateCell) {
        if (alwaysCreateCell) {
            return createCellAndFill(index);
        } else {
            Cell cell = row.getCell(index);
            return cell == null ? createCellAndFill(index) : setCellAndFill(cell, index);
        }
    }

    Cell useOrCreateCell(int index) {
        return useOrCreateCell(index, false);
    }

    Cell nextCell(int offset, int rowspan, int colspan) {
        return createCell(nextIndexOfCell(offset, rowspan, colspan));
    }
}
