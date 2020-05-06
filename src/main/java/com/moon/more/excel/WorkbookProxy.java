package com.moon.more.excel;

import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ref.FinalAccessor;
import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.HashMap;
import java.util.Map;

import static com.moon.more.excel.WorkbookType.*;

/**
 * @author benshaoye
 */
final class WorkbookProxy {

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
        if (XLSX.test(workbook)) {
            type = XLSX;
        } else if (XLS.test(workbook)) {
            type = XLS;
        } else if (SUPER.test(workbook)) {
            type = SUPER;
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

    RichTextString createRichText(String content) {
        return getWorkbookType().newRichText(content);
    }

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
        return appendRow ? setSheet(sheet, sheet.getLastRowNum() + 1) : setSheet(sheet, 0);
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

    Sheet useSheet() { return useSheet(null); }

    Sheet useSheet(String sheetName) { return useSheet(sheetName, DEFAULT_APPEND_DATA); }

    Sheet useSheet(int index) { return useSheet(index, DEFAULT_APPEND_DATA); }

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
        int index = appendCell ? row.getLastCellNum() : 0;
        return setRow(row, Math.max(index, 0));
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
        CellRangeAddress region = null;
        if (rowspan > 1 || colspan > 1) {
            int fr = rowIdx - 1;
            int lr = fr + rowspan - 1;
            region = new CellRangeAddress(fr, lr, nCellIdx, eCellIdx - 1);
            sheet.addMergedRegion(region);
        }
        mergedOnCell.replaceAs(region);
        this.indexOfCell = eCellIdx;
        return nCellIdx;
    }

    Cell setCell(Cell cell) { return this.cell = cell; }

    Cell getCell() { return cell; }

    CellRangeAddress getRegion() { return mergedOnCell.get(); }

    Cell useCell(int index) { return setCell(row.getCell(index)); }

    Cell createCell(int index) { return setCell(row.createCell(index)); }

    Cell useOrCreateCell(int index) {
        Cell cell = row.getCell(index);
        return setCell(cell == null ? row.createCell(index) : cell);
    }

    Cell nextCell() { return nextCell(0); }

    Cell nextCell(int skip) { return nextCell(skip, 1, 1); }

    Cell nextCell(int rowspan, int colspan) { return nextCell(0, rowspan, colspan); }

    Cell nextCell(int skip, int rowspan, int colspan) { return createCell(nextIndexOfCell(skip, rowspan, colspan)); }
}
