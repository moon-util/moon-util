package com.moon.office.excel.enums;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
public enum ExcelType implements Supplier<Workbook>, Predicate<String> {
    XLS(".xls") {
        @Override
        public Workbook get() {
            return new HSSFWorkbook();
        }
    },
    XLSX(".xlsx") {
        @Override
        public Workbook get() {
            return new XSSFWorkbook();
        }
    },
    SUPER(".xlsx") {
        @Override
        public Workbook get() {
            return new SXSSFWorkbook();
        }
    };

    @Override
    public boolean test(String name) {
        return name == null ? false : name.toLowerCase().endsWith(suffix);
    }

    private final String suffix;

    ExcelType(String suffix) {
        this.suffix = suffix;
    }
}
