package com.moon.more.excel.annotation;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

import java.util.function.BiConsumer;

/**
 * @author benshaoye
 */
public interface CellStyleFontBuilder extends CellStyleBuilder, BiConsumer<CellStyle, Font> {

    /**
     * 自定义样式和字体
     *
     * @param style 样式
     * @param font  字体
     */
    @Override
    void accept(CellStyle style, Font font);

    /**
     * 自定义样式设置
     * <p>
     * 这个默认实现并不会调用，所以不用担心上面的 font 是否会报{@code NPE}
     *
     * @param style 样式
     */
    @Override
    default void accept(CellStyle style) { accept(style, null); }
}
