package com.moon.mapping.processing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author benshaoye
 */
class PropertyModelTestTest {

    @Test
    void testCapitalize() {
        assertEquals("aType", PropertyModel.capitalize("aType"));
        assertEquals("BType", PropertyModel.capitalize("BType"));
        assertEquals("B", PropertyModel.capitalize("b"));
        assertEquals("A", PropertyModel.capitalize("A"));
        assertEquals("Username", PropertyModel.capitalize("Username"));
        assertEquals("Password", PropertyModel.capitalize("password"));
    }
}