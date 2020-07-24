package com.moon.poi.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author moonsky
 */
final class CellStyleProxy implements BiConsumer<Cell, CellRangeAddress>, Consumer<Row> {

    private final CellStyle style;
    private final BorderStyle tBorder;
    private final BorderStyle rBorder;
    private final BorderStyle bBorder;
    private final BorderStyle lBorder;
    private final short tColor;
    private final short rColor;
    private final short bColor;
    private final short lColor;

    CellStyleProxy(CellStyle style) {
        this.style = style;

        this.tBorder = style.getBorderTop();
        this.rBorder = style.getBorderRight();
        this.bBorder = style.getBorderBottom();
        this.lBorder = style.getBorderLeft();

        this.tColor = style.getTopBorderColor();
        this.rColor = style.getRightBorderColor();
        this.bColor = style.getBottomBorderColor();
        this.lColor = style.getLeftBorderColor();
    }

    private static boolean isBordered(BorderStyle style) { return style != null && style != BorderStyle.NONE; }

    private void setBorder4Merged(CellRangeAddress region, Sheet sheet) {
        BorderStyle border;
        if (isBordered(border = this.tBorder)) {
            RegionUtil.setBorderTop(border, region, sheet);
            RegionUtil.setTopBorderColor(tColor, region, sheet);
        }
        if (isBordered(border = this.rBorder)) {
            RegionUtil.setBorderRight(border, region, sheet);
            RegionUtil.setRightBorderColor(rColor, region, sheet);
        }
        if (isBordered(border = this.bBorder)) {
            RegionUtil.setBorderBottom(border, region, sheet);
            RegionUtil.setBottomBorderColor(bColor, region, sheet);
        }
        if (isBordered(border = this.lBorder)) {
            RegionUtil.setBorderLeft(border, region, sheet);
            RegionUtil.setLeftBorderColor(lColor, region, sheet);
        }
    }

    @Override
    public void accept(Cell cell, CellRangeAddress region) {
        cell.setCellStyle(style);
        if (region != null) {
            setBorder4Merged(region, cell.getSheet());
        }
    }

    @Override
    public void accept(Row row) { row.setRowStyle(style); }
}
