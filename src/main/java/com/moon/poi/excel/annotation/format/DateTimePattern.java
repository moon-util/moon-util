package com.moon.poi.excel.annotation.format;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

/**
 * 只能注解在：
 * <p>
 * {@link Date}、{@link java.sql.Date}、{@link Time}、{@link Timestamp};
 * <p>
 * {@link LocalDateTime}、{@link LocalDate}、{@link LocalTime};
 * <p>
 * {@link Calendar} 等字段上，注解在其他字段上自动忽略
 *
 * @author moonsky
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DateTimePattern {

    /**
     * 日期格式
     *
     * @return
     */
    String value() default "";

    /**
     * 语言
     *
     * @return 根据系统自动获取
     */
    LocaleStrategy locale() default LocaleStrategy.DEFAULT;
}
