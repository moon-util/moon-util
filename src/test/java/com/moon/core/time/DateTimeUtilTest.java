package com.moon.core.time;

import com.moon.core.enums.Const;
import com.moon.core.util.DateUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author benshaoye
 */
public class DateTimeUtilTest {

    public DateTimeUtilTest() {}

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtil.yyyy_MM_dd_HH_mm_ss_SSS);
    static DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(Const.PATTERN_DATE);

    @Test
    void testStartingOfMonth() {
        LocalDateTime datetime = LocalDateTime.now();
        System.out.println(DateTimeUtil.format(datetime, Const.PATTERN));
        System.out.println(DateTimeUtil.format(DateTimeUtil.startingOfMonth(datetime), Const.PATTERN));
        System.out.println(DateTimeUtil.endingOfYear(datetime).format(formatter));
        System.out.println(DateTimeUtil.endingOfMonth(datetime).format(formatter));
        System.out.println(DateTimeUtil.startingOfDay(datetime).format(formatter));
        System.out.println(DateTimeUtil.endingOfYear(datetime.toLocalDate()).format(patternDate));
        System.out.println(DateTimeUtil.endingOfDay(datetime).format(formatter));
        System.out.println(DateTimeUtil.endingOfYear(datetime).getNano());
        System.out.println(DateTimeUtil.startingOfDay(datetime).getNano());
    }
}
