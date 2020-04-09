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
    XLS(HSSFWorkbook::new),
    XLSX(XSSFWorkbook::new),
    SUPER(SXSSFWorkbook::new),
    ;

    private final Supplier<Workbook> creator;

    ExcelType(Supplier<Workbook> creator) {this.creator = creator;}

    @Override
    public Workbook get() { return creator.get(); }
}
