package com.moon.more.excel.annotation;

import com.moon.more.excel.annotation.def.FontDefinition;
import com.moon.more.excel.annotation.def.StyleDefinition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 可以注解在 field、setter、getter 方法上，
 * <p>
 * <h3>1. write</h3>
 * 使用 field、getter 方法上的注解；
 * <p>
 * 如果同时在 field 上和 getter 上定义了注解，使用 getter 的定义；
 *
 * <p>
 * <h3>2. read</h3>
 * 使用 field、setter 方法上的注解；
 * <p>
 * 如果同时在 field 上和 setter 上定义了注解，使用 setter 的定义；
 * <p>
 *
 * <h3>1、合并标题设置方式：</h4>
 * <pre>
 *     public class DataDTO {
 *
 *         &#064;SheetColumn(value = {"Basic", "Name"})
 *         private String name;
 *
 *         &#064;SheetColumn(value = {"Basic", "Sex"})
 *         private String sex;
 *
 *         &#064;SheetColumn(value = {"Basic", "Age"})
 *         private String age;
 *
 *         &#064;SheetColumn(value = {"Contact", "Mobile"})
 *         private String mobile;
 *
 *         &#064;SheetColumn(value = {"Contact", "Email"})
 *         private String email;
 *
 *         // ignore getters & setters
 *     }
 * </pre>
 * 将被渲染成：
 * <pre>
 *     |------------------|----------------|
 *     |      Basic       |     Contact    |
 *     |------------------|----------------|
 *     | Name | Sex | Age | Mobile | Email |
 *     |----------------- |----------------|
 * </pre>
 * <p>
 *
 * <h3>2、如果同时设置了{@link #order()}</h3>
 * 将首先按表头分组，然后对分组内的列，按{@code order}的值进行排序
 * {@link TableColumnFlatten#order()}
 *
 * <h3>3、如果表头数量不一致：</h3>
 * <pre>
 *     public class DataDTO {
 *
 *         &#064;SheetColumn(value = {"Basic", "Name"})
 *         private String name;
 *
 *         &#064;SheetColumn(value = {"Basic", "Sex"})
 *         private String sex;
 *
 *         &#064;SheetColumn(value = {"Email"})
 *         private String age;
 *
 *         // ignore getters & setters
 *     }
 * </pre>
 * 将被渲染成：
 * <pre>
 *     |------------|-------|
 *     |    Basic   |       |
 *     |------------| Email |
 *     | Name | Sex |       |
 *     |------------|-------|
 * </pre>
 * 如果出现了数量不一致会自动按最大表头行数合并行
 *
 * @author benshaoye
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
     * 当前列索引
     *
     * @return 索引号
     */
    int order() default -1;

    /**
     * 默认值
     *
     * @return 默认值
     */
    String defaultValue() default "";



    /**
     * 应用预定义样式到单元格
     *
     * @return 样式类名
     *
     * @see StyleDefinition
     * @see FontDefinition
     */
    String classname4Cell() default "";

    /**
     * 应用预定义样式到该列表头
     *
     * @return 样式类名
     *
     * @see StyleDefinition
     * @see FontDefinition
     */
    String classname4Head() default "";

    /**
     * -1 代表不设置
     * <p>
     * 如果{@ode width}大于等于 255*256 代表自动宽度，如：{@link Integer#MAX_VALUE}
     * <p>
     * 与{@link #autoWidth()}任一符合自动宽度条件都将设置为自动宽度
     *
     * @return 列宽度
     *
     * @see org.apache.poi.ss.usermodel.Sheet#setColumnWidth(int, int)
     * @see org.apache.poi.ss.usermodel.Sheet#autoSizeColumn(int, boolean)
     */
    int width() default -1;

    /**
     * 当前列是否设置自动宽度
     * <p>
     * 与{@link #width()}任一符合自动宽度条件都将设置为自动宽度
     *
     * @return 是否自动宽度
     *
     * @see org.apache.poi.ss.usermodel.Sheet#autoSizeColumn(int, boolean)
     */
    boolean autoWidth() default false;

    /**
     * 是否隐藏当前列
     * <p>
     * 隐藏列的数据会被渲染，但不会显示
     *
     * @return 是否隐藏
     */
    boolean hidden() default false;
}
