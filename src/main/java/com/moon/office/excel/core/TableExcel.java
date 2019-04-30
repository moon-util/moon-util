package com.moon.office.excel.core;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableExcel {

    String var() default "";

    TableSheet[] value() default {};

    Type type() default Type.XLS;

    /**
     * 样式还有坑没填完
     *
     * @return
     */
    TableStyle[] styles() default {};

    enum Type implements Supplier<Workbook>, Predicate<String> {
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

        Type(String suffix) {
            this.suffix = suffix;
        }
    }
}
