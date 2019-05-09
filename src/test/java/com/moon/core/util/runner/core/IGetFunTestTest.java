package com.moon.core.util.runner.core;

import com.moon.core.util.runner.Runner;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

import static com.moon.core.util.runner.core.ParseUtil.parse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author benshaoye
 */
class IGetFunTestTest {

    String str, str1;
    Runner runner, runner1;
    Object data, data1, res, res1;

    Object toValue(String expression, Object data) {
        this.data = data;
        str = expression;
        runner = parse(expression);
        res = runner.run(data);
        return res;
    }

    Object toValue(String expression) {
        return toValue(expression, null);
    }

    @Test
    void testFunctionsName() {
        toValue("@ str . indexOf (12,1)");
        assertSame(DataNum.class, runner.getClass());
        assertSame(DataNum.class, runner.getClass());
        assertEquals(res, 0);

        data = new HashMap() {{
            put("str", 12);
        }};
        toValue("@ str . indexOf (str,1)", data);
        assertEquals(res, 0);

        toValue("@now()");
        assertSame(Long.class, res.getClass());

        toValue("@now.year()");
        assertSame(Integer.class, res.getClass());
        assertEquals(res,  LocalDate.now().getYear());

        toValue("@now.month()");
        assertSame(Integer.class, res.getClass());
        assertEquals(res,  LocalDate.now().getMonthValue());

        toValue("@now.day()");
        assertSame(Integer.class, res.getClass());
        assertEquals(res,  LocalDate.now().getDayOfMonth());

        toValue("@now.hour()");
        assertSame(Integer.class, res.getClass());
        assertEquals(res,  LocalTime.now().getHour());

        toValue("@now.minute()");
        assertSame(Integer.class, res.getClass());
        assertEquals(res,  LocalTime.now().getMinute());

        toValue("@now.second()");
        assertSame(Integer.class, res.getClass());
        assertEquals(res,  LocalTime.now().getSecond());
    }
}