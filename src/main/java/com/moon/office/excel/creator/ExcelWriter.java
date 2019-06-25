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
public class ExcelWriter {

    private final WorkFactory factory;

    private final SheetWriter writer;

    private final Workbook workbook;

    /*
     * --------------------------------------------------------------
     * constructor
     * --------------------------------------------------------------
     */

    public ExcelWriter(Workbook workbook) {
        factory = new WorkFactory(workbook);
        writer = new SheetWriter(factory);
        this.workbook = workbook;
    }

    public ExcelWriter(ExcelType type) { this(type.get());}

    public ExcelWriter() { this(ExcelType.XLS); }

    public final static ExcelWriter of() { return ofXls(); }

    public final static ExcelWriter ofXls() { return of(ExcelType.XLS); }

    public final static ExcelWriter ofXlsx() { return of(ExcelType.XLSX); }

    public final static ExcelWriter ofSuper() { return of(ExcelType.SUPER); }

    public final static ExcelWriter of(ExcelType type) { return new ExcelWriter(type); }

    /*
     * --------------------------------------------------------------
     * creator
     * --------------------------------------------------------------
     */

    private ExcelWriter withSheet(Sheet sheet, Consumer<SheetWriter> consumer) {
        consumer.accept(writer.with(sheet));
        return this;
    }

    public ExcelWriter withSheet(int sheetIndex, Consumer<SheetWriter> consumer) {
        return withSheet(factory.withSheet(sheetIndex), consumer);
    }

    public ExcelWriter withSheet(String sheetName, Consumer<SheetWriter> consumer) {
        return withSheet(factory.withSheet(sheetName), consumer);
    }

    public ExcelWriter createSheet(Consumer<SheetWriter> consumer) {
        return withSheet(factory.createSheet(), consumer);
    }

    public ExcelWriter createSheet(String sheetName, Consumer<SheetWriter> consumer) {
        return withSheet(factory.createSheet(sheetName), consumer);
    }

    /*
     * --------------------------------------------------------------
     * font and style
     * --------------------------------------------------------------
     */

    public ExcelWriter createFont(String name) {

        return this;
    }

    public ExcelWriter createStyle(String name) {

        return this;
    }

    /*
     * --------------------------------------------------------------
     * write
     * --------------------------------------------------------------
     */

    public void write(OutputStream out) {
        try {
            factory.getWorkbook().write(out);
        } catch (IOException e) {
            ThrowUtil.doThrow(e);
        }
    }

    public void closeAfterWriting(OutputStream out) {
        try (OutputStream stream = out) {
            factory.getWorkbook().write(stream);
        } catch (IOException e) {
            // ignore
        }
    }

    public void closeAfterWriting(Supplier<? extends OutputStream> getter) { closeAfterWriting(getter.get()); }
}
