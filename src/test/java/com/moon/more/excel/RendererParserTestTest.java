package com.moon.more.excel;

import com.moon.core.util.DateUtil;
import com.moon.core.util.ListUtil;
import com.moon.more.excel.annotation.DataColumn;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author benshaoye
 */
class RendererParserTestTest {

    public static class Employee {

        @DataColumn
        private String name;
        private String sex;

        public int getAge() {
            return 20;
        }
    }

    @Test
    void testParsePropInfoMap() {
        Date date0 = DateUtil.parse("2019-02-01", "yyyy-MM-dd");
        Date date1 = DateUtil.parse("2020-04-01", "yyyy-MM-dd");
        Date date2 = DateUtil.parse("2019-09-01", "yyyy-MM-dd");
        Date date3 = DateUtil.parse("2019-05-01", "yyyy-MM-dd");
        List<Date> dates = ListUtil.newArrayList(date1, date2, date3, date0);
        dates.sort(Comparator.reverseOrder());
        String formatted = DateUtil.format(dates.get(0), "yyyy-MM-dd");
        Assertions.assertEquals("2020-04-01", formatted);
    }
}