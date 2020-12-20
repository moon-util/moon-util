package com.moon.mapper.convert;

import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * @author moonsky
 */
class SelectorTestTest {

    public interface NameGetter {

        String getName();
    }

    @Data
    public static class EmployeeDetail implements NameGetter {

        private String name;
        private Date createDate;
    }

    @Test
    void testSelect() throws Exception {
        // Selector.select()
        //     .column(EmployeeDetail::getName)
        //     .column(EmployeeDetail::getCreateDate)
        //     .columns(EmployeeDetail::getCreateDate, EmployeeDetail::getName)
        //     .columns(EmployeeDetail.class, NameGetter.class)
        //     .from(EmployeeDetail.class)
        //     .leftJoin(EmployeeDetail.class)
        //     .on(EmployeeDetail::getName)
        //     .eq(EmployeeDetail::getName);
    }
}