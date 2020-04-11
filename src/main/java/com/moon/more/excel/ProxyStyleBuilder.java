package com.moon.more.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 样式构建器
 *
 * @author benshaoye
 */
class ProxyStyleBuilder extends ProxyBuilder<Workbook, CellStyle> {

    /**
     * 不包含字体
     */
    private final Consumer<CellStyle> nonFontBuilder;
    /**
     * 包含字体
     */
    private final BiConsumer<CellStyle, Font> hasFontBuilder;

    ProxyStyleBuilder(String classname, Consumer<CellStyle> builder) {
        super(classname);
        this.nonFontBuilder = builder;
        this.hasFontBuilder = null;
    }

    ProxyStyleBuilder(String classname, BiConsumer<CellStyle, Font> builder) {
        super(classname);
        this.hasFontBuilder = builder;
        this.nonFontBuilder = null;
    }

    /**
     * 构建样式
     *
     * @param workbook 工作表
     *
     * @return CellStyle 实例
     */
    @Override
    CellStyle build(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        if (hasFontBuilder != null) {
            Font font = workbook.createFont();
            hasFontBuilder.accept(style, font);
        } else if (nonFontBuilder != null) {
            nonFontBuilder.accept(style);
        }
        return style;
    }
}
