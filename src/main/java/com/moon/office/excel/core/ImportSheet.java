package com.moon.office.excel.core;

/**
 * @author benshaoye
 */
public @interface ImportSheet {
    ImportRow[] value() default {};
}
