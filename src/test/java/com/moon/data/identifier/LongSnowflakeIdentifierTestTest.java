package com.moon.data.identifier;

import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
class LongSnowflakeIdentifierTestTest {

    @Test
    void testNextId() throws Exception {
        LongSnowflakeIdentifier identifier = LongSnowflakeIdentifier.of();
        for (int i = 0; i < 100; i++) {
            System.out.println(identifier.nextId());
        }

        System.out.println(String.valueOf(identifier.nextId()).length());
    }
}