package com.moon.more.excel.annotation.style;

import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnGroup;
import org.apache.poi.ss.usermodel.*;

import java.lang.annotation.*;

/**
 * 定义样式;
 * <p>
 * 由于样式采用的是定义 + 使用这种声明式设置，不必要求一定要注解在指定字段上才起效
 * 样式可以定义在任何有效列（被{@link TableColumn}或{@link TableColumnGroup}注解的列）或当前实体的类上
 *
 * @author moonsky
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
     * <p>
     * 1. 注解在字段或{@code getter}上没有指定{@code classname}，
     * 默认作用于当前字段，且不会注册到作用域内;
     * 2. 注解在类上，需要主动指定类名（不指定也行，怎么设置就怎么用）
     * <p>
     * 说明：注解在字段或{@code getter}上，并且没有主动指定{@code classname}和{@link UseStyleOnRow}
     * 那么，这个定义默认会作用于当前列数据
     *
     * @return 类名
     */
    String classname() default "";

    /**
     * 自定义样式创建器，这个具有更高的优先级
     * <p>
     * 设置了自定义样式类，其他属性均会被忽略
     *
     * @return 自定义类
     *
     * @see StyleBuilder 自定义样式;{@link CellStyle}
     * @see StyleFontBuilder 自定义样式和字体;{@link Font}
     */
    Class<? extends StyleBuilder> createBy() default StyleBuilder.class;

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
     * 引用字体
     *
     * @return
     *
     * @see DefinitionFont#id()
     */
    String refFontId() default "";

    /**
     * 前景色色值
     * <p>
     * POI 中实际上前景色是背景色，背景色是什么？不知道
     *
     * @return 色值
     *
     * @see IndexedColors
     * @see CellStyle#setFillForegroundColor(short)
     */
    short foregroundColor() default -1;

    /**
     * 背景色色值
     *
     * @return 色值
     *
     * @see IndexedColors
     * @see CellStyle#setFillBackgroundColor(short)
     */
    short backgroundColor() default -1;

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
     * 是否换行
     *
     * @return true: 允许换行
     */
    boolean wrapText() default false;

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
