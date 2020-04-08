package com.moon.more.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
class StyleBuilder {

    private final String classname;
    private Consumer<CellStyle> nonFontBuilder;
    private BiConsumer<CellStyle, Font> hasFontBuilder;

    StyleBuilder(String classname, Consumer<CellStyle> builder) {
        this.nonFontBuilder = builder;
        this.classname = classname;
    }

    StyleBuilder(String classname, BiConsumer<CellStyle, Font> builder) {
        this.hasFontBuilder = builder;
        this.classname = classname;
    }

    String getClassname() { return classname; }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        StyleBuilder that = (StyleBuilder) o;
        return Objects.equals(getClassname(), that.getClassname());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClassname());
    }
}
