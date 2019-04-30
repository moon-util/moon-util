package com.moon.core.net.enums;

import com.moon.core.enums.EnumDescriptor;

/**
 * @author benshaoye
 */
public enum Protocol implements EnumDescriptor {
    HTTP("http:"),
    HTTPS("https:"),
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
        for (int i = 0; i < protocols.length; i++) {
            if (protocols[i].isProtocolWith(url)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getText() {
        return protocol;
    }
}
