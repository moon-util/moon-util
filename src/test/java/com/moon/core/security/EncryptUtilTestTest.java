package com.moon.core.security;

import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class EncryptUtilTestTest {

    @Test
    void testMd5() {
        System.out.println(EncryptUtil.sha1(EncryptUtil.md5("12345678")));
        System.out.println(EncryptUtil.md5("12345678"));
        System.out.println(EncryptUtil.sha1("12345678"));
        System.out.println(EncryptUtil.sha256("12345678"));
        System.out.println(EncryptUtil.sha384("12345678"));
        System.out.println(EncryptUtil.sha512("12345678"));
    }
}