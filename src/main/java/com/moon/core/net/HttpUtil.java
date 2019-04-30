package com.moon.core.net;

import com.moon.core.lang.ThrowUtil;

/**
 * @author benshaoye
 * @date 2018/9/14
 */
public final class HttpUtil {

    /**
     * default http request timeout
     */
    final static int TIMEOUT = 60000;

    private HttpUtil() {
        ThrowUtil.noInstanceError();
    }

    public static HttpConnector get(String url) {
        return null;
    }
}
