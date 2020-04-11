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
    /**
     * Excel 2003
     */
    XLS(new XlsGetter()),
    /**
     * Excel 2007
     */
    XLSX(new XlsxGetter()),
    /**
     * Excel 2007 用于超大文件
     * <p>
     * 一般情况下极少用到
     */
    SUPER(new SuperGetter());
    /**
     * excel workbook 创建器
     */
    private final Supplier<Workbook> creator;

    ExcelType(Supplier<Workbook> creator) { this.creator = creator; }

    /**
     * 创建空工作簿
     *
     * @return an empty workbook
     */
    @Override
    public Workbook get() { return creator.get(); }

    /**
     * 创建 ExcelFactory
     *
     * @return ExcelFactory
     */
    public WorkbookFactory newFactory() { return new WorkbookFactory(get()); }

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
