package com.moon.more.excel.annotation;

import org.apache.poi.ss.usermodel.Font;

import java.lang.annotation.*;

/**
 * @author benshaoye
 * @see Font
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DefinitionFont.List.class)
public @interface DefinitionFont {

    String fontName() default "";

    short color() default -1;

    boolean bold() default false;

    boolean italic() default false;

    boolean strikeout() default false;

    short fontHeight() default -1;

    short fontHeightInPoints() default -1;

    byte underline() default Font.U_NONE;

    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {

        DefinitionFont[] value();
    }
}
