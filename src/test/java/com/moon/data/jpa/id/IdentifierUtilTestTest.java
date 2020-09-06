package com.moon.data.jpa.id;

import com.moon.core.lang.LongUtil;
import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
class IdentifierUtilTestTest {


    @Test
    void testNewInstance() throws Exception {
        System.out.println(System.currentTimeMillis());
        System.out.println(LongUtil.toString(System.currentTimeMillis(), 62));

        // newInstance(TimestampOrderedStringIdentifier.class);
        // newInstance("TimestampOrderedIdentifier");
        // newInstance("SnowflakeIdentifier");
        // System.out.println(newInstance("com.moon.spring.jpa.identity.ThisIdentifier:30:20"));
        // System.out.println(newInstance("com.moon.spring.jpa.identity.StringIdentifier:30:20"));
    }

    // private IdentifierGenerator newInstance(Class targetType) {
    //     return newInstance(targetType.getName());
    // }
    //
    // private IdentifierGenerator newInstance(String targetTypeName) {
    //     IdentifierGenerator generator = IdentifierUtil.newInstance(targetTypeName);
    //     Assertions.assertNotNull(generator);
    //     return generator;
    // }
}