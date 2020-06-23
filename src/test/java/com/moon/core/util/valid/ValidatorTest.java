package com.moon.core.util.valid;

import com.moon.core.util.RandomStringUtil;
import com.moon.core.util.RandomUtil;
import com.moon.core.util.validator.Validator;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
public class ValidatorTest {

    public ValidatorTest() {
    }


    static class Employee {

        private String name = RandomStringUtil.nextChinese(2, 4);
        private int age = RandomUtil.nextInt(10, 50);
        private String address;
    }

    @Test
    void testEmployee() throws Exception {
        Validator.of(new Employee())
            // .setImmediate(true)
            .require(e -> e.address != null, "地址不能为空")
            .require(e -> e.age > 40, "年龄不能小于 40 岁").get();
    }
}
