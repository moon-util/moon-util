package com.moon.core.lang;

import java.util.Base64;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 * @date 2018/9/18
 */
public final class Base64Util {

    private final static String DEFAULT_TO_BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    private final static String DEFAULT_TO_BASE64URL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";

    private Base64Util() {
        noInstanceError();
    }

    public final static String toBase64(String normal) {
        return Base64.getEncoder().encodeToString(normal.getBytes());
    }

    public static String toString(String base64) {
        return new String(Base64.getDecoder().decode(base64));
    }

    public final static String toBase64() {
        return ThrowUtil.rejectAccessError();
    }

    public static final class Encoder {
        private final byte[] newline;
        private final int lineMax;
        private final boolean isURL;
        private final boolean doPadding;

        public Encoder(byte[] newline, int lineMax, boolean isURL, boolean doPadding) {
            this.doPadding = doPadding;
            this.newline = newline;
            this.lineMax = lineMax;
            this.isURL = isURL;
        }

        private int encode0(String base64, byte[] src, int off, int end, byte[] dst) {
            return encode0(base64.toCharArray(), src, off, end, dst);
        }

        private int encode0(char[] base64, byte[] src, int off, int end, byte[] dst) {
            int sp = off;
            int slen = (end - off) / 3 * 3;
            int sl = off + slen;
            if (lineMax > 0 && slen  > lineMax / 4 * 3) {
                slen = lineMax / 4 * 3;
            }
            int dp = 0;
            while (sp < sl) {
                int sl0 = Math.min(sp + slen, sl);
                for (int sp0 = sp, dp0 = dp ; sp0 < sl0; ) {
                    int bits = (src[sp0++] & 0xff) << 16 |
                        (src[sp0++] & 0xff) <<  8 |
                        (src[sp0++] & 0xff);
                    dst[dp0++] = (byte)base64[(bits >>> 18) & 0x3f];
                    dst[dp0++] = (byte)base64[(bits >>> 12) & 0x3f];
                    dst[dp0++] = (byte)base64[(bits >>> 6)  & 0x3f];
                    dst[dp0++] = (byte)base64[bits & 0x3f];
                }
                int dlen = (sl0 - sp) / 3 * 4;
                dp += dlen;
                sp = sl0;
                if (dlen == lineMax && sp < end) {
                    for (byte b : newline){
                        dst[dp++] = b;
                    }
                }
            }
            // 1 or 2 leftover bytes
            if (sp < end) {
                int b0 = src[sp++] & 0xff;
                dst[dp++] = (byte)base64[b0 >> 2];
                if (sp == end) {
                    dst[dp++] = (byte)base64[(b0 << 4) & 0x3f];
                    if (doPadding) {
                        dst[dp++] = '=';
                        dst[dp++] = '=';
                    }
                } else {
                    int b1 = src[sp++] & 0xff;
                    dst[dp++] = (byte)base64[(b0 << 4) & 0x3f | (b1 >> 4)];
                    dst[dp++] = (byte)base64[(b1 << 2) & 0x3f];
                    if (doPadding) {
                        dst[dp++] = '=';
                    }
                }
            }
            return dp;
        }
    }

    public static class Decoder {}

    private static class Support {}
}
