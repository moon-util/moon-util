package com.moon.core.security;

import com.moon.core.exception.DefaultException;
import com.moon.core.lang.StringUtil;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
public enum EncryptType implements Supplier<MessageDigest> {
    MD5,
    SHA_1,
    SHA_256,
    SHA_384,
    SHA_512,
    ;

    public static MessageDigest forName(String name) {
        try {
            return MessageDigest.getInstance(name);
        } catch (NoSuchAlgorithmException e) {
            throw new DefaultException(e);
        }
    }

    public static String encrypt(String input, MessageDigest digest) {
        try {
            input = StringUtil.requireNotBlank(input).trim();
            digest.update(input.getBytes("UTF-8"));
            return toHexValue(digest.digest());
        } catch (UnsupportedEncodingException e) {
            throw new DefaultException(e);
        }
    }

    public String encrypt(String input) { return encrypt(input, get()); }

    private final static String toHexValue(byte[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; ++i) {
            sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100), 1, 3);
        }
        return sb.toString();
    }

    @Override
    public MessageDigest get() { return forName(name().replace('_', '-')); }
}
