package com.moon.more.excel.annotation;

import org.apache.poi.ss.usermodel.Cell;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * 返回值类型只能在以下类型中一种（建议使用 java 中的泛型明确指定返回值类型）：
 * <p>
 * 1. String（或{@link CharSequence}的实现类，对应{@link Cell}类型为『字符串』
 * 3. Double（或{@link Number}的实现类，对应{@link Cell}类型为『数字』
 * 4. Boolean，对应{@link Cell}类型为『Boolean』
 * 5. Date（或{@link Date}的子类，对应{@link Cell}类型为『日期』
 * 6. Calendar（或{@link Calendar}的子类，对应{@link Cell}类型为『日期』
 * 7. LocalDateTime（或{@link LocalDateTime}的子类，对应{@link Cell}类型为『日期』
 * 8. LocalDate（或{@link LocalDate}的子类，对应{@link Cell}类型为『日期』
 *
 * @author benshaoye
 */
@FunctionalInterface
public interface FieldTransformer<F, R> {

    /**
     * 返回转换后的值
     *
     * @param fieldValue 字段值
     *
     * @return 转换后的值
     */
    R transform(F fieldValue);
}
