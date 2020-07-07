package com.moon.core.util;

import lombok.Data;
import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
class ValidationUtilTestTest {

    @Data
    public static class UserDetail {

        private String name;

        private String address;

        private String homePageUrl;

        private int age;
    }

    @Test
    void testOf() throws Exception {
    }
}