package com.moon.office.excel.core;

/**
 * @author benshaoye
 */
public @interface ImportRow {
    ImportCell[] value() default {};
}
