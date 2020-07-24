package com.moon.poi.excel.annotation.style;

import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.ss.usermodel.Font;

import java.lang.annotation.*;

/**
 * @author moonsky
 * @see Font
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DefinitionFont.List.class)
public @interface DefinitionFont {

    /**
     * @return
     *
     * @see DefinitionStyle#refFontId()
     */
    String id() default "";

    /**
     * 字体名，默认不设置
     *
     * @return
     *
     * @see Font#setFontName(String)
     */
    String fontName() default "";

    /**
     * 字体颜色
     *
     * @return
     *
     * @see Font#setColor(short)
     */
    short color() default -1;

    /**
     * 是否加粗
     *
     * @return
     *
     * @see Font#setBold(boolean)
     */
    boolean bold() default false;

    /**
     * 斜体
     *
     * @return
     *
     * @see Font#setItalic(boolean)
     */
    boolean italic() default false;

    /**
     * 划线
     *
     * @return
     *
     * @see Font#setStrikeout(boolean)
     */
    boolean strikeout() default false;

    /**
     * 字高
     *
     * @return
     *
     * @see Font#setFontHeight(short)
     */
    short fontHeight() default -1;


    /**
     * 字高
     *
     * @return
     *
     * @see Font#setFontHeightInPoints(short)
     */
    short fontHeightInPoints() default -1;

    /**
     * @return
     *
     * @see Font#setTypeOffset(short)
     * @see Font#setUnderline(byte)
     */
    byte underline() default Font.U_NONE;

    /**
     * @return
     *
     * @see Font#setCharSet(int)
     * @see FontCharset
     */
    int charset() default -1;

    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {

        DefinitionFont[] value();
    }
}
