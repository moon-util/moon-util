package com.moon.office.excel.core;

import com.moon.core.lang.ThrowUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

/**
 * @author benshaoye
 */
final class CellStyleUtil {
    private CellStyleUtil() {
        ThrowUtil.noInstanceError();
    }

    final static void setRowStyle(Workbook workbook, Sheet sheet, CellRangeAddress range, Row row, TableStyle[] styles) {
        row.setRowStyle(createStyle(workbook, sheet, range, styles));
    }

    final static void setCellStyle(
        Workbook workbook, Sheet sheet, CellRangeAddress range, Cell cell, TableStyle[] styles
    ) {
        cell.setCellStyle(createStyle(workbook, sheet, range, styles));
    }

    private final static CellStyle createStyle(
        Workbook workbook, Sheet sheet, CellRangeAddress range, TableStyle[] styles
    ) {
        CellStyle cellStyle = workbook.createCellStyle();
        int length = styles.length;
        TableStyle style;
        for (int i = 0; i < length; i++) {
            if ((style = styles[i]) == null) {
                continue;
            }
            if (i == 0) {
                setCellColor(cellStyle, style);
                setAlignment(cellStyle, style);
            } else {
                overrideCellColor(cellStyle, style);
                overrideAlignment(cellStyle, style);
            }
            setBorder(cellStyle, style, sheet, range);
        }
        return cellStyle;
    }

    private static void overrideCellColor(CellStyle cellStyle, TableStyle style) {
        short color = style.backgroundColor();
        if (color > -1) {
            cellStyle.setFillForegroundColor(color);
        }
        FillPatternType type = style.patternType();
        if (type != Const.DEFAULT_FILL) {
            cellStyle.setFillPattern(type);
        }
    }

    private static void overrideAlignment(CellStyle cellStyle, TableStyle style) {
        if (style.align() != Const.DEFAULT_ALIGN) {
            cellStyle.setAlignment(style.align());
        }
        if (style.verticalAlign() != Const.DEFAULT_ALIGN_V) {
            cellStyle.setVerticalAlignment(style.verticalAlign());
        }
    }

    private static void setCellColor(CellStyle cellStyle, TableStyle style) {
        cellStyle.setFillForegroundColor(style.backgroundColor());
        cellStyle.setFillPattern(style.patternType());
    }

    private static void setAlignment(CellStyle cellStyle, TableStyle style) {
        cellStyle.setAlignment(style.align());
        cellStyle.setVerticalAlignment(style.verticalAlign());
    }

    private static void setBorder(CellStyle cellStyle, TableStyle style, Sheet sheet, CellRangeAddress range) {
        setBorderColor(cellStyle, style, sheet, range);
        setBorderStyle(cellStyle, style, sheet, range);
    }

    private static void setBorderColor(CellStyle cellStyle, TableStyle style, Sheet sheet, CellRangeAddress range) {
        short[] colors = style.borderColor();
        switch (colors.length) {
            case 0:
                break;
            case 1:
                setBorderColor(cellStyle, sheet, range, colors[0], colors[0], colors[0], colors[0]);
                break;
            case 2:
                setBorderColor(cellStyle, sheet, range, colors[0], colors[1], colors[0], colors[1]);
                break;
            case 3:
                setBorderColor(cellStyle, sheet, range, colors[0], colors[1], colors[2], colors[1]);
                break;
            default:
                setBorderColor(cellStyle, sheet, range, colors[0], colors[1], colors[2], colors[3]);
                break;
        }
    }

    private static void setBorderStyle(CellStyle cellStyle, TableStyle style, Sheet sheet, CellRangeAddress range) {
        BorderStyle[] borderStyles = style.borderStyle();
        switch (borderStyles.length) {
            case 0:
                break;
            case 1:
                setBorderStyle(cellStyle, sheet, range, borderStyles[0], borderStyles[0], borderStyles[0], borderStyles[0]);
                break;
            case 2:
                setBorderStyle(cellStyle, sheet, range, borderStyles[0], borderStyles[1], borderStyles[0], borderStyles[1]);
                break;
            case 3:
                setBorderStyle(cellStyle, sheet, range, borderStyles[0], borderStyles[1], borderStyles[2], borderStyles[1]);
                break;
            default:
                setBorderStyle(cellStyle, sheet, range, borderStyles[0], borderStyles[1], borderStyles[2], borderStyles[3]);
                break;
        }
    }

    private final static void setBorderStyle(
        CellStyle style, Sheet sheet, CellRangeAddress range,
        BorderStyle top, BorderStyle right, BorderStyle bottom, BorderStyle left
    ) {
        style.setBorderTop(top);
        style.setBorderRight(right);
        style.setBorderBottom(bottom);
        style.setBorderLeft(left);
        if (range != null) {
            RegionUtil.setBorderTop(top, range, sheet);
            RegionUtil.setBorderRight(right, range, sheet);
            RegionUtil.setBorderBottom(bottom, range, sheet);
            RegionUtil.setBorderLeft(left, range, sheet);
        }
    }

    private final static void setBorderColor(
        CellStyle style, Sheet sheet, CellRangeAddress range,
        short top, short right, short bottom, short left
    ) {
        style.setTopBorderColor(top);
        style.setRightBorderColor(right);
        style.setBottomBorderColor(bottom);
        style.setLeftBorderColor(left);
        if (range != null) {
            RegionUtil.setTopBorderColor(top, range, sheet);
            RegionUtil.setRightBorderColor(right, range, sheet);
            RegionUtil.setBottomBorderColor(bottom, range, sheet);
            RegionUtil.setLeftBorderColor(left, range, sheet);
        }
    }
}
