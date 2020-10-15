package com.moon.core.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class JoinerTestTest {

    @Test
    void testOf() throws Exception {
        String joined = Joiner.of("-").add(12).add(23).join(1, 2, 3).toString();
        assertEquals("12-23-1-2-3", joined);

        joined = Joiner.of("-", "(", ")").add(12).add(23).join(1, 2, 3).toString();
        assertEquals("(12-23-1-2-3)", joined);

        joined = Joiner.of("-", "(", "").add(12).add(23).join(1, 2, 3).toString();
        assertEquals("(12-23-1-2-3", joined);

        joined = Joiner.of("-", "", "=").add(12).add(23).join(1, 2, 3).toString();
        assertEquals("12-23-1-2-3=", joined);

        joined = Joiner.of("-", "", "=").add(12).append(56).add(23).join(1, 2, 3).toString();
        assertEquals("1256-23-1-2-3=", joined);

        joined = Joiner.of("-", "", "=").add(12).appendDelimiter().append(56).add(23).join(1, 2, 3).toString();
        assertEquals("12-56-23-1-2-3=", joined);
    }
}