package com.moon.core.mail;

/**
 * @author moonsky
 */
public class EmailInbox {

    private EmailAccount account;

    /**
     * 构造函数
     *
     * @param account
     */
    public EmailInbox(EmailAccount account) {
        this.account = account;
    }

    public static EmailInbox of(EmailAccount account) {
        return new EmailInbox(account);
    }
}
