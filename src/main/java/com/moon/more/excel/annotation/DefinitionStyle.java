package com.moon.more.excel.annotation;

import org.apache.poi.ss.usermodel.*;

import java.lang.annotation.*;

/**
 * 定义样式
 *
 * @author benshaoye
 * @see CellStyle
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DefinitionStyle.List.class)
public @interface DefinitionStyle {

    /**
     * 唯一类名
     * <p>
     * 类名可以是空字符串
     *
     * @return 类名
     */
    String classname() default "";

    /**
     * 继承自另外一个样式{@link #classname()}
     * <p>
     * 继承的类名如果设置空字符串将被视为无效，直接忽略；
     * <p>
     * 如果继承的目标{@link #classname()}是空字符串，请为它指定一个非空{@code classname}；
     *
     * @return 所继承的类名
     *
     * @see CellStyle#cloneStyleFrom(CellStyle)
     */
    String extendBy() default "";

    /**
     * 自定义样式创建器，这个具有更高的优先级
     * <p>
     * 设置了自定义样式类，其他属性均会被忽略
     *
     * @return 自定义类
     *
     * @see CellStyleBuilder 自定义样式;{@link CellStyle}
     * @see CellStyleFontBuilder 自定义样式和字体;{@link Font}
     */
    Class<? extends CellStyleBuilder> createBy() default CellStyleBuilder.class;

    /**
     * 前景色色值
     * <p>
     * POI 中实际上前景色是背景色，背景色是什么？不知道
     *
     * @return 色值
     *
     * @see IndexedColors
     */
    short fillForegroundColor() default -1;

    /**
     * 背景色色值
     *
     * @return 色值
     *
     * @see IndexedColors
     */
    short fillBackgroundColor() default -1;

    /**
     * 填充模式
     *
     * @return default NO_FILL
     */
    FillPatternType fillPattern() default FillPatternType.NO_FILL;

    /**
     * 垂直对齐
     *
     * @return default CENTER
     */
    VerticalAlignment verticalAlign() default VerticalAlignment.CENTER;

    /**
     * 水平对齐
     *
     * @return default LEFT
     */
    HorizontalAlignment align() default HorizontalAlignment.LEFT;

    /**
     * top, right, bottom, left
     * <p>
     * 边框样式
     *
     * @return default empty
     */
    BorderStyle[] border() default {};

    /**
     * top, right, bottom, left
     * <p>
     * -1 是无效值，可用作占位符，跳过设置
     *
     * @return 边框色值列表
     *
     * @see IndexedColors
     */
    short[] borderColor() default {};

    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {

        DefinitionStyle[] value();
    }
}
