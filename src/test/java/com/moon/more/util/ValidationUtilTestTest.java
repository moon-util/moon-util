package com.moon.more.util;

import org.junit.jupiter.api.Test;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class ValidationUtilTestTest {

    public static class User {
        @NotNull
        @NotBlank(message = "姓名不能为空")
        private String name;
    }

    @Test
    void testValidate() {
        System.out.println(ValidationUtil.validate(new User()));
    }
}