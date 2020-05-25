package com.moon.core.net.enums;

import com.moon.core.enums.EnumDescriptor;

/**
 * @author benshaoye
 */
public enum Protocol implements EnumDescriptor {
    /**
     * http 协议
     */
    HTTP("http:"),
    /**
     * https 协议
     */
    HTTPS("https:"),
    /**
     * ftp 协议
     */
    FTP("ftp:"),
    ;

    private final String protocol;

    Protocol(String protocol) {
        this.protocol = protocol;
    }

    public boolean isProtocolWith(String url) {
        return url.startsWith(getText());
    }

    public static boolean matchProtocolWith(String url) {
        Protocol[] protocols = values();
        for (Protocol value : protocols) {
            if (value.isProtocolWith(url)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getText() { return protocol; }
}
