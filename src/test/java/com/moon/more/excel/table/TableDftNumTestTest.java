package com.moon.more.excel.table;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class TableDftNumTestTest {

    public static class Construct {}

    @Test
    void testGetConstruct() throws Exception {
        System.out.println(Construct.class.getConstructor());
        System.out.println(Construct.class.getDeclaredConstructor());
    }
}