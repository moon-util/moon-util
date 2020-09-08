package com.moon.poi.excel;

import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ref.FinalAccessor;
import com.moon.core.util.CollectUtil;
import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;

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

    // 图片管理器
    private Table<Sheet, Integer, List<IntConsumer>> pictureManagerTable;

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

    public Table<Sheet, Integer, List<IntConsumer>> getPictureManagerTable() {
        return pictureManagerTable;
    }

    public Table<Sheet, Integer, List<IntConsumer>> ensurePictureManagerTable() {
        return pictureManagerTable == null ? (pictureManagerTable = new TableImpl<>()) : pictureManagerTable;
    }

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

    ClientAnchor getAnchor() {
        FinalAccessor<CellRangeAddress> region = this.mergedOnCell;
        if (region.isAbsent()) {
            Cell cell = getCell();
            int rowIdx = cell.getRowIndex();
            int colIdx = cell.getColumnIndex();
            return getWorkbookType().newAnchor(0, 0, 0, 0, colIdx, rowIdx, colIdx + 1, rowIdx + 1);
        } else {
            CellRangeAddress address = region.get();
            int col1 = address.getFirstColumn();
            int col2 = address.getLastColumn();
            int row1 = address.getFirstRow();
            int row2 = address.getLastRow();
            return getWorkbookType().newAnchor(0, 0, 0, 0, col1, row1, col2 + 1, row2 + 1);
        }
    }

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
        return setSheet(sheet, appendRow ? sheet.getLastRowNum() : 0);
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
        return purelyUseRow(row, index, appendCell);
    }

    Row purelyUseRow(Row row, int index, boolean appendCell) {
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

    int getColumnWidth() {
        return sheet.getColumnWidth(getCell().getColumnIndex());
    }

    void setColumnWidth(Sheet sheet, int index, int width) {
        sheet.setColumnWidth(index, width);
        Table<Sheet, Integer, List<IntConsumer>> table = getPictureManagerTable();
        if (table != null) {
            List<IntConsumer> consumers = table.get(sheet, index);
            if (consumers != null) {
                for (IntConsumer consumer : consumers) {
                    consumer.accept(width);
                }
            }
        }
    }

    boolean isAllowAutoWidth(Sheet sheet, int index) {
        Table<Sheet, Integer, List<IntConsumer>> table = getPictureManagerTable();
        return table == null || CollectUtil.isEmpty(table.get(sheet, index));
    }

    IntConsumer getIndexedColumnRunner(BufferedImage image) {
        final Row row = this.getRow();
        final double w = image.getWidth(), h = image.getHeight();
        final double scale = h / w, compatibleScale = scale / 3;
        // System.out.println("Width: " + w + ", Height: " + h + ", Scale: " + scale);
        return width -> {
            // System.out.println(row.getHeight());
            // System.out.println(width + "\t\t" + (width * scale) + "\t\t" + ((short) (width * scale)));
            row.setHeight((short) (width * compatibleScale));
        };
    }

    final void writeImageOnCell(BufferedImage image) {
        IntConsumer consumer = getIndexedColumnRunner(image);
        final int width = getColumnWidth();
        try {
            Sheet sheet = getSheet();
            Drawing drawing = sheet.createDrawingPatriarch();
            ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", byteArr);
            ClientAnchor anchor = getAnchor();
            byte[] bytes = byteArr.toByteArray();
            int picture = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
            drawing.createPicture(anchor, picture);

            // 执行器
            Table table = ensurePictureManagerTable();
            int columnIndex = getCell().getColumnIndex();
            List list = (List) table.get(sheet, columnIndex);
            if (list == null) {
                list = new ArrayList();
                table.put(sheet, columnIndex, list);
            }
            list.add(consumer);
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        } finally {
            consumer.accept(width);
        }
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

    Cell getCell() { return cell; }

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
