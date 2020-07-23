package com.moon.core.mail;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @author moonsky
 */
class EmailUtilTestTest {

    @Test
    @Disabled
    void testLogin() throws Exception {
        EmailAccount account = null;
        Email email = account.newEmail();
        email.title("标题")
            .content("<div>这是自动发送的邮件<div>")
            .isHtml(false)
            .addAttachment(new File("D:/member-detail.xlsx"))
            .toUser("xua744531854@163.com")
            .send(true);
    }
}