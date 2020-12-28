package com.moon.core.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author moonsky
 */
public class GroupUtilTest {

    public static class Employee{

        public String group() {
            return null;
        }
    }

    public GroupUtilTest() {
    }

    @Test
    void testSimplifyGroup() throws Exception {
        List<Employee> list = new ArrayList<>();
        GroupUtil.simplifyGroup(list, Employee::group);
    }
}
