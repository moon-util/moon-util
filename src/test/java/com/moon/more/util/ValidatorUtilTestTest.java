package com.moon.more.util;

import com.moon.core.enums.Testers;
import com.moon.core.lang.StringUtil;
import com.moon.core.model.KeyValue;
import com.moon.core.util.RequireValidateException;
import com.moon.more.validator.ValidatorUtil;
import com.moon.more.validator.annotation.AllInInts;
import com.moon.more.validator.annotation.InInts;
import com.moon.more.validator.annotation.ResidentID18;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    public static class UserInfo {

        @ResidentID18
        private String certNo;
    }

    @Test
    void testCertNo() throws Exception {
        UserInfo info = new UserInfo();

        Set set = ValidatorUtil.validate(info);
        assertTrue(set.isEmpty());

        info.certNo = "";
        set = ValidatorUtil.validate(info);
        Assertions.assertFalse(set.isEmpty());
        System.out.println(set);
    }

    @Test
    void testValidateValue() throws Exception {
        User user = new User();
        Set<ConstraintViolation<User>> validated = ValidatorUtil.validate(user);
        Assertions.assertFalse(validated.isEmpty());
        System.out.println(validated);

        user.age0 = 19;
        validated = ValidatorUtil.validate(user);
        assertTrue(validated.isEmpty());

        User0 user0 = new User0();
        Set<ConstraintViolation<User0>> validated0 = ValidatorUtil.validate(user0);
        assertTrue(validated0.isEmpty());

        User1 user1 = new User1();
        Set<ConstraintViolation<User1>> validated1 = ValidatorUtil.validate(user1);
        assertTrue(validated1.isEmpty());

        User2 user2 = new User2();
        Set<ConstraintViolation<User2>> validated2 = ValidatorUtil.validate(user2);
        assertTrue(validated2.size() == 1);
        System.out.println(validated2);
    }

    @Test
    void testValidateField() throws Exception {
        assertThrows(IllegalArgumentException.class,() -> {
            String validatedString = ValidatorUtil.of("123456").when(str -> str != null, validator -> {
                validator.require(str -> str.length() > 6, "长度不能小于6");
            }).setExceptionBuilder(RequireValidateException::new).get();
            System.out.println(validatedString);
        });
        assertThrows(IllegalArgumentException.class,() -> {
            ValidatorUtil.ofNullableCollect(new ArrayList<>()).requireCountOf(3, Testers.isNotNull).get();
        });
    }

    @Test
    void testValidate() throws Exception {
    }
}