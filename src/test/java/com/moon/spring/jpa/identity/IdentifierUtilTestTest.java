package com.moon.spring.jpa.identity;

import org.hibernate.id.IdentifierGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
class IdentifierUtilTestTest {

    @Test
    void testNewInstance() throws Exception {
        newInstance(TimestampOrderedStringIdentifier.class);
        newInstance("TimestampOrderedIdentifier");
        newInstance("SnowflakeIdentifier");
        System.out.println(newInstance("com.moon.spring.jpa.identity.ThisIdentifier:30:20"));
        System.out.println(newInstance("com.moon.spring.jpa.identity.StringIdentifier:30:20"));
    }

    private IdentifierGenerator newInstance(Class targetType) {
        return newInstance(targetType.getName());
    }

    private IdentifierGenerator newInstance(String targetTypeName) {
        IdentifierGenerator generator = IdentifierUtil.newInstance(targetTypeName);
        Assertions.assertNotNull(generator);
        return generator;
    }
}