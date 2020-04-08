package com.moon.more.excel;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class ExcelUtil {

    private ExcelUtil() { noInstanceError(); }

    public static ExcelFactory xls() { return of(ExcelType.XLS); }

    public static ExcelFactory xlsx() { return of(ExcelType.XLSX); }

    public static ExcelFactory superExcel() { return of(ExcelType.SUPER); }

    public static ExcelFactory of(ExcelType type) { return new ExcelFactory(type.get()); }
}
