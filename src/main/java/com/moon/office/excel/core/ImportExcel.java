package com.moon.office.excel.core;

/**
 * @author benshaoye
 */
public @interface ImportExcel {
    ImportSheet[] value() default {};

    Class target();
}
