package com.moon.office.excel.core;

import com.moon.office.excel.enums.ValueType;

/**
 * @author benshaoye
 */
public @interface TableCell {

    String var() default "";

    String value() default "";

    String when() default "true";

    String colspan() default "1";

    String rowspan() default "1";

    String skipCells() default "0";

    String[] delimiters() default {};

    ValueType type() default ValueType.STRING;

    String className() default "";

    int width() default -1;

    short height() default -1;
}
