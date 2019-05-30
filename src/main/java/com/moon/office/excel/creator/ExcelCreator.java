package com.moon.office.excel.creator;

import com.moon.core.lang.ThrowUtil;
import com.moon.office.excel.ExcelType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
public class ExcelCreator extends BaseCreator {

    private final Workbook workbook;

    /*
     * --------------------------------------------------------------
     * constructor
     * --------------------------------------------------------------
     */

    public ExcelCreator(Workbook workbook) {this.workbook = workbook;}

    public ExcelCreator(ExcelType type) { this(type.get());}

    public ExcelCreator() { this(ExcelType.XLS); }

    public final static ExcelCreator of() { return of(ExcelType.XLS); }

    public final static ExcelCreator ofXls() { return of(ExcelType.XLS); }

    public final static ExcelCreator ofXlsx() { return of(ExcelType.XLSX); }

    public final static ExcelCreator ofSuper() { return of(ExcelType.SUPER); }

    public final static ExcelCreator of(ExcelType type) { return new ExcelCreator(type); }

    /*
     * --------------------------------------------------------------
     * creator
     * --------------------------------------------------------------
     */

    private ExcelCreator withSheet(Sheet sheet, Consumer<SheetCreator> consumer) {
        consumer.accept(new SheetCreator(sheet));
        return this;
    }

    public ExcelCreator withSheet(int sheetIndex, Consumer<SheetCreator> consumer) {
        return withSheet(workbook.getSheetAt(sheetIndex), consumer);
    }

    public ExcelCreator withSheet(String sheetName, Consumer<SheetCreator> consumer) {
        return withSheet(workbook.getSheet(sheetName), consumer);
    }

    public ExcelCreator createSheet(Consumer<SheetCreator> consumer) {
        return withSheet(workbook.createSheet(), consumer);
    }

    public ExcelCreator createSheet(String sheetName, Consumer<SheetCreator> consumer) {
        return withSheet(workbook.createSheet(sheetName), consumer);
    }

    /*
     * --------------------------------------------------------------
     * font and style
     * --------------------------------------------------------------
     */

    public ExcelCreator createFont(String name) {

        return this;
    }

    public ExcelCreator createStyle(String name) {
        
        return this;
    }

    /*
     * --------------------------------------------------------------
     * write
     * --------------------------------------------------------------
     */

    public void write(OutputStream out) {
        try {
            workbook.write(out);
        } catch (IOException e) {
            ThrowUtil.doThrow(e);
        }
    }

    public void closeAfterWriting(OutputStream out) {
        try (OutputStream stream = out) {
            workbook.write(stream);
        } catch (IOException e) {
            ThrowUtil.doThrow(e);
        }
    }

    public void closeAfterWriting(Supplier<? extends OutputStream> getter) { closeAfterWriting(getter.get()); }
}
