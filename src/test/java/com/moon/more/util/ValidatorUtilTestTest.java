package com.moon.more.util;

import com.moon.more.validator.ValidatorUtil;
import com.moon.more.validator.annotation.AllInInts;
import com.moon.more.validator.annotation.InInts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * @author moonsky
 */
class ValidatorUtilTestTest {

    public static class User {

        @InInts(values = "1800,19,20")
        private int age0;
    }

    public static class User0 {

        @InInts(values = "1800,19,20,0")
        private int[] age0;
    }

    public static class User1 {

        @AllInInts(values = "1800,19,20,0")
        private int[] age0 = {};
    }

    public static class User2 {

        @AllInInts(values = "1800,19,20,0")
        private int[] age0 = {1};

        @AllInInts(values = "1800,19,20,0,1")
        private int[] age1 = {1};
    }

    public static class User3 {

        @AllInInts(values = "1800,19,20,0", nullable = false)
        private Integer[] age0 = {1, null};
    }


    @Test
    void testValidateValue() throws Exception {
        User user = new User();
        Set<ConstraintViolation<User>> validated = ValidatorUtil.validate(user);
        Assertions.assertFalse(validated.isEmpty());
        System.out.println(validated);

        user.age0 = 19;
        validated = ValidatorUtil.validate(user);
        Assertions.assertTrue(validated.isEmpty());

        User0 user0 = new User0();
        Set<ConstraintViolation<User0>> validated0 = ValidatorUtil.validate(user0);
        Assertions.assertTrue(validated0.isEmpty());

        User1 user1 = new User1();
        Set<ConstraintViolation<User1>> validated1 = ValidatorUtil.validate(user1);
        Assertions.assertTrue(validated1.isEmpty());

        User2 user2 = new User2();
        Set<ConstraintViolation<User2>> validated2 = ValidatorUtil.validate(user2);
        Assertions.assertTrue(validated2.size() == 1);
        System.out.println(validated2);
    }

    @Test
    void testValidateField() throws Exception {
    }

    @Test
    void testValidate() throws Exception {
    }
}