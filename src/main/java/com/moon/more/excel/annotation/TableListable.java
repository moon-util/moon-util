package com.moon.more.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注该列数据是可迭代列，此注解只是表示数据是可迭代数据，以及合并策略等
 * <p>
 * 但如果没有定义{@link TableColumn}或{@link TableColumnFlatten}，将不会渲染
 * <p>
 * 一个实体里最多只能有一列数据可迭代
 *
 * @author benshaoye
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableListable {

    /**
     * 参考列
     * <p>
     * read 时，参考列单元格值相同的为同一组
     * <p>
     * write 时无意义
     * <p>
     * 如：
     * <p>
     * 此例演示了以身份证号列为参考，相同身份证号代表同一个学生的成绩列表，即时姓名不一致
     * <p>
     * 如果需要参考两列，只需要定义为数组即可
     *
     * <pre>
     * public class Student {
     *
     *     &#064;TableColumn("姓名")
     *     private String name;
     *
     *     &#064;TableColumn("身份证号")
     *     private String idCard;
     *
     *     &#064;TableListable(value = "身份证号")
     *     private List&lt;String&gt; scores;
     * }
     * </pre>
     *
     * @return 列名
     */
    String[] reference() default "";

    /**
     * 外部数据行合并策略
     * <p>
     * write 时: 外部数据行合并策略
     * <p>
     * read 时: 无意义
     *
     * @return 默认每一行都渲染
     */
    MergeStrategy mergeStrategy() default MergeStrategy.FILL_EVERY;
}
