package com.moon.more.excel.annotation;

import org.apache.poi.ss.usermodel.Cell;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
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
     * 默认值
     *
     * @return 默认值
     */
    String defaultValue() default "";

    /**
     * 什么情况下设置默认值{@link #defaultValue()}
     * <pre>
     * 1. 针对字符串还有:
     * 1.1. null 或空字符串: {@link DefaultStrategy#EMPTY}
     * 1.2. null 、空字符串或空白字符串: {@link DefaultStrategy#BLANK}
     * 2. 针对数字还有:
     * 2.1. 值为 0: {@link DefaultStrategy#ZERO}；
     * 2.2. 负数: {@link DefaultStrategy#NEGATIVE}
     * 2.3. 正数: {@link DefaultStrategy#POSITIVE}
     * </pre>
     * 基本数据类型不可能为 null，所以用在基本数据类型上时需要注意这点
     *
     * @return 默认字段值设置策略
     */
    DefaultStrategy defaultFor() default DefaultStrategy.NULL;

    /**
     * 自定义检测策略，这个是为了满足更多情况默认值或字段值转换设计的，比如隐藏敏感信息等；
     * <p>
     * 优先级高于{@link #defaultFor()}，故当字段设置了这个，上面的默认值设置就无效
     * <p>
     * 如果自定义了{@link #transformBy()}，将始终按照指定的类型检测和执行，
     * 返回值类型只能在以下类型中一种（建议使用 java 中的泛型明确指定返回值类型）：
     *
     * 1. String（或{@link CharSequence}的实现类，对应{@link Cell}类型为『字符串』
     * 3. Double（或{@link Number}的实现类，对应{@link Cell}类型为『数字』
     * 4. Boolean，对应{@link Cell}类型为『Boolean』
     * 5. Date（或{@link Date}的子类，对应{@link Cell}类型为『日期』
     * 6. Calendar（或{@link Calendar}的子类，对应{@link Cell}类型为『日期』
     * 7. LocalDateTime（或{@link LocalDateTime}的子类，对应{@link Cell}类型为『日期』
     * 8. LocalDate（或{@link LocalDate}的子类，对应{@link Cell}类型为『日期』
     * <p>
     *
     *
     * <pre>
     *     public class TestPredicate implements Predicate {
     *
     *         //
     *         public boolean text(Object fieldValue) {
     *
     *         }
     *     }
     *
     *     public class Employee {
     *
     *         @ TableColumn(defaultForClass = TextPredicate.class)
     *         private String password;
     *     }
     *
     * </pre>
     *
     * @return
     *
     * @see #defaultFor() {@link #transformBy()}优先于{@link #defaultFor()}
     */
    Class<? extends TableColumnTransformer> transformBy() default TableColumnTransformer.class;
}
