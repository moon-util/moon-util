package com.moon.more.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class SheetFactory extends BaseFactory<Row, SheetFactory> {

    private final RowFactory factory;

    private Row row;

    public SheetFactory(WorkbookProxy proxy) {
        super(proxy);
        this.factory = new RowFactory(proxy);
    }

    @Override
    Row get() { return getRow(); }

    void setRow(Row row) { this.row = row; }

    public Row getRow() {
        Row sheet = this.row;
        if (sheet == null) {
            this.setRow(sheet = proxy.getRow());
        }
        return sheet;
    }

    private RowFactory getRowFactory() { return this.factory; }

    public SheetFactory setColumnsWidth(Integer... widths) {
        int len = widths == null ? 0 : widths.length;
        if (len > 0) {
            Integer columnWidth;
            Sheet sheet = proxy.getSheet();
            for (int i = 0; i < len; i++) {
                if ((columnWidth = widths[i]) != null) {
                    sheet.setColumnWidth(i, columnWidth);
                }
            }
        }
        return this;
    }

    public SheetFactory setWidth(int columnIndex, int width) {
        proxy.getSheet().setColumnWidth(columnIndex, width);
        return this;
    }

    public SheetFactory active() {
        Workbook workbook = proxy.getWorkbook();
        workbook.setActiveSheet(workbook.getSheetIndex(proxy.getSheet()));
        return this;
    }

    public SheetFactory row(Consumer<RowFactory> consumer) {
        setRow(proxy.nextRow());
        consumer.accept(getRowFactory());
        return this;
    }

    public SheetFactory row(int skipRows, Consumer<RowFactory> consumer) {
        setRow(proxy.nextRow(skipRows));
        consumer.accept(getRowFactory());
        return this;
    }
}
