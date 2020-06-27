package com.moon.more.util;

import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author benshaoye
 */
class ValidationUtilTestTest {

    private static class Address {

        @NotNull
        @Length(min = 12)
        private String address;
    }

    public static class User {

        @NotNull
        @NotBlank(message = "姓名不能为空")
        private String name;
        @Min(24)
        private int age;
        @Valid
        @NotNull
        private Address address = new Address();
    }

    @Test
    void testValidate() {
        Set set = ValidationUtil.validate(new User());
        Assertions.assertFalse(set.isEmpty());
        System.out.println(set);
    }
}