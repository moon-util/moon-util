package com.moon.core.util.runner.core;

import com.moon.core.util.require.Requires;
import com.moon.core.util.runner.Runner;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

import static com.moon.core.util.runner.core.ParseUtil.parse;

/**
 * @author benshaoye
 */
class IGetFunTestTest {

    static final Requires REQUIRES = Requires.of();

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
        REQUIRES.requireSame(runner.getClass(), DataNum.class);
        REQUIRES.requireEquals(res, 0);

        data = new HashMap() {{
            put("str", 12);
        }};
        toValue("@ str . indexOf (str,1)", data);
        REQUIRES.requireEquals(res, 0);

        toValue("@now()");
        REQUIRES.requireInstanceOf(res, Long.class);

        toValue("@now.year()");
        REQUIRES.requireInstanceOf(res, Integer.class);
        REQUIRES.requireEquals(res, LocalDate.now().getYear());

        toValue("@now.month()");
        REQUIRES.requireInstanceOf(res, Integer.class);
        REQUIRES.requireEquals(res, LocalDate.now().getMonthValue());

        toValue("@now.day()");
        REQUIRES.requireInstanceOf(res, Integer.class);
        REQUIRES.requireEquals(res, LocalDate.now().getDayOfMonth());

        toValue("@now.hour()");
        REQUIRES.requireInstanceOf(res, Integer.class);
        REQUIRES.requireEquals(res, LocalTime.now().getHour());

        toValue("@now.minute()");
        REQUIRES.requireInstanceOf(res, Integer.class);
        REQUIRES.requireEquals(res, LocalTime.now().getMinute());

        toValue("@now.second()");
        REQUIRES.requireInstanceOf(res, Integer.class);
        REQUIRES.requireEquals(res, LocalTime.now().getSecond());
    }
}