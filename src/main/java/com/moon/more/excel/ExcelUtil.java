package com.moon.more.excel;

import com.moon.core.util.TypeUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.function.Supplier;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.more.excel.ExcelType.*;

/**
 * @author benshaoye
 */
public final class ExcelUtil {

    private ExcelUtil() { noInstanceError(); }

    public static ExcelFactory xls() { return of(XLS); }

    public static ExcelFactory xlsx() { return of(XLSX); }

    public static ExcelFactory superExcel() { return of(SUPER); }

    public static ExcelFactory of(ExcelType type) { return of(type.get()); }

    public static ExcelFactory of(Supplier<Workbook> creator) { return of(creator.get()); }

    public static ExcelFactory of(Workbook workbook) { return new ExcelFactory(workbook); }

    public static Object getValue(Cell cell) {
        CellType type = cell.getCellType();
        switch (type) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return cell.getNumericCellValue();
            case ERROR:
                return cell.getErrorCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case BLANK:
            case _NONE:
            default:
                return null;
        }
    }

    public static <T> T getValueAs(Cell cell, Class<T> type) {
        if (cell == null || type == null) { return null; }
        Object value = getValue(cell);
        return TypeUtil.cast().toType(value, type);
    }
}
