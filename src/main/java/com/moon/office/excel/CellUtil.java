package com.moon.office.excel;

import com.moon.core.lang.StringUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

import java.util.Date;
import java.util.function.Function;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class CellUtil {

    private CellUtil() { noInstanceError(); }

    public static String getAsString(Cell cell) { return getAsString(cell, StringUtil::stringifyOrNull); }

    public static String getAsString(Cell cell, Function<Object, String> caster) { return getAsType(cell, caster); }

    public static <T> T getAsType(Cell cell, Function<Object, T> caster) { return caster.apply(getValue(cell)); }

    public static Object getValue(Cell cell) {
        if (cell == null) { return null; }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return DateUtil.isCellDateFormatted(cell)
                    ? cell.getDateCellValue()
                    : cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            case ERROR:
                return cell.getErrorCellValue();
            default:
                return null;
        }
    }

    public static String getString(Cell cell) { return cell == null ? null : cell.getStringCellValue(); }

    public static boolean getBoolean(Cell cell) { return cell == null ? false : cell.getBooleanCellValue(); }

    public static double getNumeric(Cell cell) { return cell == null ? 0 : cell.getNumericCellValue(); }

    public static Date getDate(Cell cell) {
        return DateUtil.isCellDateFormatted(cell) ? null : cell.getDateCellValue();
    }
}
