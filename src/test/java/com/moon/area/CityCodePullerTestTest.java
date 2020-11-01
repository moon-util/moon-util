package com.moon.area;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author moonsky
 */
class CityCodePullerTestTest {

    @Test
    void testOf() throws Exception {
        assertThrows(Exception.class, () -> {
            CityCodePuller.of(2017);
        });
        CityCodePuller.of(2018);
    }

    @Test
    void testExtraYearlyUrlsList() throws Exception {
        Map<String, YearlyUrlModel> yearly = CityCodePuller.getYearlyUrlList(2018);
        for (Map.Entry entry : yearly.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }
}