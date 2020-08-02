package com.moon.core.net;

import com.moon.core.lang.ThrowUtil;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author moonsky
 */
public final class URLUtil {

    private URLUtil() { ThrowUtil.noInstanceError(); }

    public static URL toURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }
}
