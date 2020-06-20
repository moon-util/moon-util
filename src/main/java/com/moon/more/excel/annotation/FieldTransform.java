package com.moon.more.excel.annotation;

import com.moon.more.excel.annotation.defaults.DefaultValue;
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
public @interface FieldTransform {

    /**
     * 自定义检测策略，这个是为了满足更多情况默认值或字段值转换设计的，比如隐藏敏感信息等；
     * <p>
     * 优先级高于{@link DefaultValue#when()}，故当字段设置了这个，上面的默认值设置就无效
     * <p>
     * 如果自定义了{@link FieldTransform}，将始终按照指定的类型检测和执行，
     * 返回值类型只能在以下类型中一种（建议使用 java 中的泛型明确指定返回值类型）：
     * <p>
     * 1. String（或{@link CharSequence}的实现类，对应{@link Cell}类型为『字符串』
     * 3. Double（或{@link Number}的实现类，对应{@link Cell}类型为『数字』
     * 4. Boolean，对应{@link Cell}类型为『Boolean』
     * 5. Date（或{@link Date}的子类，对应{@link Cell}类型为『日期』
     * 6. Calendar（或{@link Calendar}的子类，对应{@link Cell}类型为『日期』
     * 7. LocalDateTime（或{@link LocalDateTime}的子类，对应{@link Cell}类型为『日期』
     * 8. LocalDate（或{@link LocalDate}的子类，对应{@link Cell}类型为『日期』
     * <p>
     *
     * <pre>
     *     public class Employee {
     *
     *         @ TableColumn()
     *         @ TableColumnTransform(value = ...)
     *         private String password;
     *     }
     *
     * </pre>
     *
     * @return
     *
     * @see DefaultValue {@link FieldTransform}优先于{@link DefaultValue}
     */
    Class<? extends FieldTransformer> value();
}
