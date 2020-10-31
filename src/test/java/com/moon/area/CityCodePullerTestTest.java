package com.moon.area;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        List<YearlyUrlModel> urlsList = CityCodePuller.getYearlyUrlList(2018);
        for (YearlyUrlModel url : urlsList) {
            System.out.println(url);
        }
    }
}