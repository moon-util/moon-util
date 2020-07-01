package com.moon.more.util;

import com.moon.more.validator.ValidatorUtil;
import com.moon.more.validator.annotation.InInts;
import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author moonsky
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
        Set set = ValidatorUtil.validate(new User());
        Assertions.assertFalse(set.isEmpty());
        System.out.println(set);
    }

    public static class Validated {

        @InInts(values = "1,2,3")
        private int value;
    }

    @Test
    void testAnnotation() {
        Validated v = new Validated();
        Set set = ValidatorUtil.validate(v);
        Assertions.assertFalse(set.isEmpty());
        System.out.println(set);

        v.value = 1;
        set = ValidatorUtil.validate(v);
        Assertions.assertTrue(set.isEmpty());
        System.out.println(set);
        v.value = 2;
        set = ValidatorUtil.validate(v);
        Assertions.assertTrue(set.isEmpty());
        System.out.println(set);
        v.value = 3;
        set = ValidatorUtil.validate(v);
        Assertions.assertTrue(set.isEmpty());
        System.out.println(set);
        v.value = 4;
        set = ValidatorUtil.validate(v);
        Assertions.assertFalse(set.isEmpty());
        System.out.println(set);
    }
}