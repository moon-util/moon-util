package com.moon.core.mail;

import com.moon.core.lang.MoonUtil;

import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.Future;

/**
 * @author moonsky
 */
class SenderExecutor {

    static Future<?> sendMail(MimeMessage message) {
        return MoonUtil.run(() -> {
            try {
                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }
}
