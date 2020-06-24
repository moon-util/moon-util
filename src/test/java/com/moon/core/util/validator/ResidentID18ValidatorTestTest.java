package com.moon.core.util.validator;

import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class ResidentID18ValidatorTestTest {

    @Test
    void testOf() {
        ResidentID18Validator.of("").isValid();
    }
}