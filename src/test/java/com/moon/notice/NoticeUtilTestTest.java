package com.moon.notice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class NoticeUtilTestTest {

    @Test
    void testNewParameters() {
        NoticeUtil.newParameters().add(NamedParameter.of("name", "张三"));
    }
}