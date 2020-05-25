package com.moon.core.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.moon.core.lang.ThrowUtil.runtime;
import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class NetworkUtil {

    private NetworkUtil() { noInstanceError(); }

    public static InetAddress getLocalhost() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            return runtime(e);
        }
    }

    public static String getLocalIP4() { return getLocalhost().getHostAddress(); }
}
