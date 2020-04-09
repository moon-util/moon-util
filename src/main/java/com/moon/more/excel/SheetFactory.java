package com.moon.more.excel;

import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class SheetFactory extends BaseFactory<Sheet, SheetFactory> {

    private final RowFactory factory;

    private Sheet sheet;

    public SheetFactory(WorkbookProxy proxy) {
        super(proxy);
        factory = new RowFactory(proxy);
    }

    public Sheet getSheet() {
        return sheet;
    }

    void setSheet(Sheet sheet) { this.sheet = sheet; }

    @Override
    Sheet get() { return getSheet(); }

    private RowFactory getRowFactory() { return this.factory; }

    @Override
    public SheetFactory definitionComment(String classname, Consumer<Comment> commentBuilder) {
        super.definitionComment(classname, commentBuilder);
        return this;
    }

    public SheetFactory columnsAutoWidth(boolean useMergedCells, int... columnIndexes) {
        if (columnIndexes != null) {
            int length = columnIndexes.length;
            for (int i = 0; i < length; i++) {
                sheet.autoSizeColumn(columnIndexes[i], useMergedCells);
            }
        }
        return this;
    }

    public SheetFactory columnsAutoWidth(int... columnIndexes) {
        if (columnIndexes != null) {
            int length = columnIndexes.length;
            for (int i = 0; i < length; i++) {
                sheet.autoSizeColumn(columnIndexes[i]);
            }
        }
        return this;
    }

    public SheetFactory setColumnsWidth(Integer... widths) {
        int len = widths == null ? 0 : widths.length;
        if (len > 0) {
            Integer columnWidth;
            Sheet sheet = getSheet();
            for (int i = 0; i < len; i++) {
                if ((columnWidth = widths[i]) != null) {
                    sheet.setColumnWidth(i, columnWidth);
                }
            }
        }
        return this;
    }

    public SheetFactory setWidth(int columnIndex, int width) {
        getSheet().setColumnWidth(columnIndex, width);
        return this;
    }

    public SheetFactory active() {
        Workbook workbook = proxy.getWorkbook();
        workbook.setActiveSheet(workbook.getSheetIndex(getSheet()));
        return this;
    }

    public SheetFactory row(Consumer<RowFactory> consumer) {
        factory.setRow(proxy.nextRow());
        consumer.accept(getRowFactory());
        return this;
    }

    public SheetFactory row(int skipRows, Consumer<RowFactory> consumer) {
        factory.setRow(proxy.nextRow(skipRows));
        consumer.accept(getRowFactory());
        return this;
    }
}
