package com.moon.mapping;

import org.joda.time.*;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * @author benshaoye
 */
public class MappingUtilTest {

    public MappingUtilTest() {}

    public static class EmployeeDetail {

        public void setName(String name) {
        }
    }

    @Test
    void testThisMapping() {
        long time = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        DateTime time0 = new DateTime(calendar);
        MutableDateTime time1 = new MutableDateTime(calendar);
        LocalDateTime time2 = new LocalDateTime(calendar);
        LocalDate time3 = new LocalDate(calendar);
        LocalTime time4 = new LocalTime(calendar);
        time0.getMillis();
        time0.toDate();
        System.out.println("====" + time0.toString("yyyy-MM-dd HH:mm:ss"));
        System.out.println("====" + time1.toString("yyyy-MM-dd HH:mm:ss"));
        System.out.println("====" + time2.toString("yyyy-MM-dd HH:mm:ss"));
        System.out.println("====" + time3.toString("yyyy-MM-dd"));
        System.out.println("====" + time4.toString("HH:mm:ss"));
    }
}
