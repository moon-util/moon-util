package com.moon.core.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.moon.core.lang.ThrowUtil.doThrow;
import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 * @date 2018/9/17
 */
public final class InternetUtil {

    private InternetUtil() { noInstanceError(); }

    public final static InetAddress getLocalhost() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            return doThrow(e);
        }
    }

    public static String getLocalIP4() { return getLocalhost().getHostAddress(); }
}
