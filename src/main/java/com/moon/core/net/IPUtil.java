package com.moon.core.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.UnknownHostException;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
@SuppressWarnings("all")
public final class IPUtil {

    private IPUtil() { noInstanceError(); }

    public static String getLocalIPAddress() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public static String getLocalIP6Address() {
        try {
            return Inet6Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }
}
