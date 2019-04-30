package com.moon.office.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.function.Function;

import static com.moon.core.lang.StringUtil.trimToNull;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface ColumnDescriptor<T> {
    /**
     * 值转换器
     *
     * @return
     */
    Function<T, ?> getConverter();

    /**
     * 默认列名 "#"
     *
     * @return
     */
    default String getTitle() {
        return "#";
    }

    /**
     * 计算值
     *
     * @param obj
     * @return
     */
    default Object computeValue(T obj) {
        return getConverter().apply(obj);
    }

    /**
     * 默认单元格值
     *
     * @return
     */
    default CharSequence getDefaultValue() {
        return null;
    }

    /**
     * 列宽
     *
     * @return
     */
    default int getWidth() {
        return 4200;
    }

    /**
     * 设置值
     *
     * @param cell
     * @param obj
     * @param defaultVal
     * @return
     */
    default Cell setCellValue(Cell cell, T obj, CharSequence defaultVal) {
        Object computed = computeValue(obj);
        String value = computed == null ? trimToNull(defaultVal) : computed.toString();
        if (value != null) {
            cell.setCellValue(value);
        }
        return cell;
    }

    /**
     * 设置值
     *
     * @param cell
     * @param obj
     * @return
     */
    default Cell setCellValue(Cell cell, T obj) {
        return setCellValue(cell, obj, getDefaultValue());
    }

    /**
     * 设置列宽
     *
     * @param sheet
     * @param index
     * @return
     */
    default Sheet setColumnWidth(Sheet sheet, int index) {
        sheet.setColumnWidth(index, getWidth());
        return sheet;
    }

    /**
     * 设置标题
     *
     * @param cell
     * @return
     */
    default Cell setTitle(Cell cell) {
        cell.setCellValue(getTitle());
        return cell;
    }
}
