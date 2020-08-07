package com.moon.poi.excel.annotation;

import com.moon.poi.excel.annotation.style.DefinitionStyle;
import com.moon.poi.excel.annotation.style.HeadStyle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author moonsky
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableColumn {

    /**
     * 列标题，可设置合并标题
     * <p>
     * 默认字段名首字母大写，如：name => Name；age => Age
     *
     * @return 列标题
     */
    String[] value() default {};

    /**
     * 表头行高，
     * <p>
     * 数量和位置与表头标题{@link #value()}对应，
     * 不足的自动用<code>-1（不设置）</code>补上，如果不打算设置某行行高也可用 -1 跳过，
     * 超出部分自动忽略，如果有通过{@link TableColumnGroup}形成多级关系，父级超出部分也不会影响子级行高；
     * <p>
     * 只有设置了标题的位置才能设置行高，否则就忽略；
     * <p>
     * 由于行高可是针对所有列，所以这样设置可将注意力放在实际要用的位置，不用考虑其他地方
     *
     * @return
     */
    short[] rowsHeight4Head() default {};

    /**
     * -1 代表不设置
     * <p>
     * 如果{@ode width}大于等于 255*256 代表自动宽度，如：{@link Integer#MAX_VALUE}
     * <p>
     * 默认字号下常见数据推荐宽度：
     * 1. 居民身份证号：5400
     * 2. 11位手机号码：3600
     * 3. 一个汉字：500（多个汉字可以 500 * N）
     * 4. 一个数字或字母(或英文符号)：100
     *
     * @return 列宽度
     *
     * @see org.apache.poi.ss.usermodel.Sheet#setColumnWidth(int, int)
     * @see org.apache.poi.ss.usermodel.Sheet#autoSizeColumn(int, boolean)
     */
    int width() default -1;

    /**
     * 列排序，order 值只用来比较大小，不要求连续
     * <p>
     * 列按 order 大小排序，相同{@code order}列一定相邻，通常按字段声明顺序排序，
     * 如果 order 相同，但{@link TableColumn}交替出现在{@code getter}或{@code field}上，
     * 可能会存在例外情况，这种情况可修改 order 大小
     *
     * @return 顺序号
     */
    int order() default 0;

    /**
     * 列偏移
     *
     * @return 偏移量
     */
    int offset() default 0;

    /**
     * 标题参与偏移的级数
     *
     * <pre>
     * 设 N = {@code offsetHeadRows}, COUNT = 表头行数;
     * 当 N >= COUNT 时，所有标题行都参与偏移，否则只偏移倒数 N 行;
     * </pre>
     *
     * @return 表头偏移级数
     */
    int offsetHeadRows() default 1;

    /**
     * 应用样式
     * <p>
     * 应用由{@link DefinitionStyle#classname()}定义的样式
     *
     * @return 样式名
     */
    String style() default "";

    /**
     * 表头样式
     *
     * @return
     */
    HeadStyle[] styleForHead() default {};
}
