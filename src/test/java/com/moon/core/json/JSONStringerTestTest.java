package com.moon.core.json;

import com.moon.core.lang.IntUtil;
import com.moon.core.util.RandomStringUtil;
import com.moon.core.util.RandomUtil;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
class JSONStringerTestTest {

    @Test
    void testStringify() throws Exception {
        int[] ints = IntUtil.toInts(1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
        String jsonStr = JSONCfg.getStringer().stringify(ints);
        System.out.println(jsonStr);
    }

    @Data
    public static class User {

        private String name = RandomStringUtil.nextChinese(2, 4);
        private int age = RandomUtil.nextInt(18, 32);
    }

    @Data
    public static class Employee {

        private String employeeName = RandomStringUtil.nextChinese(2, 4);
        private String employeeAddress = RandomStringUtil.nextChinese(5, 8);
    }

    @Test
    void testStringifyArr() throws Exception {
        int[] ints = IntUtil.toInts(1, 2, 3, 4, 5, 6, 7, 8, 9, 0);

        Map<String, Object> map = new HashMap<>();
        map.put("employee1", new Employee());
        map.put("employee2", new Employee());
        map.put("score", ints);
        map.put("date", LocalDateTime.now());

        Object[] users = {new User(), new User(), map, new Date(), ints};
        StringBuilder jsonStr = JSONCfg.getStringer().stringify(new StringBuilder(),users, new StringifySettings(0));
        System.out.println(jsonStr);
    }
}