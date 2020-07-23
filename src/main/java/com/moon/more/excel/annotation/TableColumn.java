package com.moon.more.excel.annotation;

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
     * 默认字段名首字母大写，如：name -&gt; Name；age -&gt; Age
     *
     * @return 列标题
     */
    String[] value() default {};

    /**
     * 表头行高，
     * <p>
     * 数量和位置与表头标题{@link #value()}对应，
     * 不足的自动用<code>-1（不设置）</code>补上，
     * 超出部分自动忽略；
     * <p>
     * 只有设置了标题的位置才能设置行高，否则就忽略
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
     * 排序顺序
     *
     * @return 顺序号
     */
    int order() default 0;

    /**
     * 单元格偏移
     *
     * @return 偏移量
     */
    int offset() default 0;

    /**
     * 标题参与偏移的行数, 当{@code offsetHeadRows}大于等于表头行数时，
     * 所有标题行都偏移，否则只偏移倒数{@code offsetHeadRows}行
     *
     * @return 表头偏移行数
     */
    int offsetHeadRows() default Integer.MAX_VALUE;
}
