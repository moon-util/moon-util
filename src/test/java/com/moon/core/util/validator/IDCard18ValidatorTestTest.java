package com.moon.core.util.validator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class IDCard18ValidatorTestTest {

    @Test
    void testOf() {
        IDCard18Validator.of("").isValid();
    }
}