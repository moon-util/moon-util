package com.moon.office.excel.core;

/**
 * @author benshaoye
 */
public @interface TableSheet {

    String var() default "";

    TableRow[] value() default {};

    String when() default "true";

    /**
     * 关于 sheetName，默认是：'新建'
     * <p>
     * 在创建一个 名为 sheetName 的 sheet 时会检测对应 sheetName 是否存在，
     * 如果存在则会创建成 sheetName(1) 的 sheet，如果 sheetName(1) 也存在，
     * 则会创建成 sheetName(2)，以此类推。
     *
     * @return
     */
    String sheetName() default "'新建'";

    String[] delimiters() default {};
}
