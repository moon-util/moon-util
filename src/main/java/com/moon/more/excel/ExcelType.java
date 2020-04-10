package com.moon.more.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.function.Supplier;

/**
 * @author benshaoye
 */
public enum ExcelType implements Supplier<Workbook> {
    XLS(new XlsGetter()),
    XLSX(new XlsxGetter()),
    SUPER(new SuperGetter());

    private final Supplier<Workbook> creator;

    ExcelType(Supplier<Workbook> creator) { this.creator = creator; }

    @Override
    public Workbook get() { return creator.get(); }

    public ExcelFactory newFactory() { return new ExcelFactory(get()); }

    static class XlsGetter implements Supplier<Workbook> {

        @Override
        public Workbook get() { return new HSSFWorkbook(); }
    }

    static class XlsxGetter implements Supplier<Workbook> {

        @Override
        public Workbook get() { return new XSSFWorkbook(); }
    }

    static class SuperGetter implements Supplier<Workbook> {

        @Override
        public Workbook get() { return new SXSSFWorkbook(); }
    }
}
