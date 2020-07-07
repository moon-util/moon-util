package com.moon.core.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.lang.ThrowUtil.runtime;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public final class IPUtil {

    private IPUtil() { noInstanceError(); }

    public static InetAddress getLocalhost() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            return runtime(e);
        }
    }

    public static String getLocalIPV4() { return getLocalhost().getHostAddress(); }

    public static String getLocalIPV6Address() {
        try {
            return Inet6Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }
}
