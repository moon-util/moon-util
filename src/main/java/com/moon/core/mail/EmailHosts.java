package com.moon.core.mail;

/**
 * @author moonsky
 */
public enum EmailHosts {
    QQ("imap.qq.com", "smtp.qq.com"),
    ;

    private final String inboxHost;
    private final String sendHost;

    EmailHosts(String inboxHost, String sendHost) {
        this.inboxHost = inboxHost;
        this.sendHost = sendHost;
    }

    public String getInboxHost() { return inboxHost; }

    public String getSendHost() { return sendHost; }
}
