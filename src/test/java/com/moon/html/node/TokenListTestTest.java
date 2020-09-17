package com.moon.html.node;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class TokenListTestTest {

    @Test
    void testAdd() {
        TokenList tokenList = new TokenList();
        tokenList.add("123");
        assertEquals(1, tokenList.size());
    }
}