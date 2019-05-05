package com.moon.core.security;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.security.EncryptType.*;

/**
 * @author benshaoye
 */
public final class EncryptUtil {
    private EncryptUtil() { noInstanceError(); }

    public final static String md5(String input) { return MD5.encrypt(input); }

    public final static String sha1(String input) { return SHA_1.encrypt(input); }

    public final static String sha256(String input) { return SHA_256.encrypt(input); }

    public final static String sha384(String input) { return SHA_384.encrypt(input); }

    public final static String sha512(String input) { return SHA_512.encrypt(input); }

    public final static String encrypt(String input, EncryptType... types) {
        for (int i = 0; i < types.length; i++) { input = types[i].encrypt(input); }
        return input;
    }

    public final static String encrypt(String input, String... types) {
        for (int i = 0; i < types.length; i++) {
            input = EncryptType.encrypt(input, EncryptType.forName(types[i]));
        }
        return input;
    }
}
