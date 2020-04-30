package com.moon.spring.jpa.identity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

/**
 * @author benshaoye
 */
class TimestampOrderedIdentifierTestTest {

    @Test
    void testGenerate() {
        TimestampOrderedStringIdentifier identifier = new TimestampOrderedStringIdentifier();
        Serializable identity = identifier.generate(null, null);
        String strId = identity.toString();
        String longId = String.valueOf(Long.parseLong(strId));
        Assertions.assertEquals(strId, longId);
        System.out.println(strId);
        System.out.println(Long.MAX_VALUE);
    }
}