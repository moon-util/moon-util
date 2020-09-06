package com.moon.data.identifier;

import com.moon.core.time.DatePatternConst;
import com.moon.core.time.DateUtil;
import com.moon.core.time.Datetime;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * @author moonsky
 */
class LongSnowflakeIdentifierTestTest {

    @Test
    void testNextId() throws Exception {
        LongSnowflakeIdentifier identifier = LongSnowflakeIdentifier.of();
        for (int i = 0; i < 100; i++) {
            System.out.println(identifier.nextId());
        }

        System.out.println(String.valueOf(identifier.nextId()).length());
    }

    @Test
    void testStartTime() throws Exception {
        System.out.println(Datetime.of(2020, 9, 1, 0, 0, 0, 0, 0).getTime());;

        final long twepoch = 1288834974657L;
        Date date = DateUtil.toDate(twepoch);
        System.out.println(DateUtil.format(date, DatePatternConst.yyyy_MM_dd_HH_mm_ss_SSS));
    }

    @Test
    void testStartTime1() throws Exception {

        final long twepoch = 1598889600000L;
        Date date = DateUtil.toDate(twepoch);
        System.out.println(DateUtil.format(date, DatePatternConst.yyyy_MM_dd_HH_mm_ss_SSS));
    }
}