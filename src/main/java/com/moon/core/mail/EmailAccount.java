package com.moon.core.mail;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.Properties;

/**
 * @author moonsky
 * @see EmailUtil
 */
public class EmailAccount implements Cloneable {

    private static final String NULL = null;

    private static final boolean FALSE = false;

    private String username;

    private String password;

    private boolean SSLEncrypt;

    private Properties properties;

    /******************** constructor ******************************************************/

    public EmailAccount(String host) {
        this(host, NULL, NULL);
    }

    public EmailAccount(String host, boolean sslEncrypt) {
        this(host, NULL, NULL, sslEncrypt);
    }

    public EmailAccount(String host, String username, String password) {
        this(host, username, password, FALSE);
    }

    public EmailAccount(String host, String username, String password, boolean sslEncrypt) {
        this.setHost(host);
        this.setSSLEncrypt(sslEncrypt);
        this.login(username, password);
    }

    public EmailAccount(String host, String username, String password, Integer timeout, boolean sslEncrypt) {
        this.setHost(host);
        this.setTimeout(timeout);
        this.setSSLEncrypt(sslEncrypt);
        this.login(username, password);
    }

    /******************** getter and setter ******************************************************/

    public String getHost() {
        return getProperty(MAIL_HOST_KEY);
    }

    public EmailAccount setHost(String host) {
        return this.setProperty(MAIL_HOST_KEY, host);
    }

    public EmailAccount login(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
        return this;
    }

    public String getUsername() {
        return username;
    }

    public EmailAccount setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public EmailAccount setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isSSLEncrypt() {
        return SSLEncrypt;
    }

    public EmailAccount setSSLEncrypt(boolean SSLEncrypt) {
        this.SSLEncrypt = SSLEncrypt;
        return this;
    }

    public Integer getTimeout() {
        return (Integer) getProperties().get(MAIL_TIMEOUT_KEY);
    }

    public EmailAccount setTimeout(Integer timeout) {
        if (timeout != null) {
            return this.setProperty(MAIL_TIMEOUT_KEY, timeout);
        }
        return this;
    }

    public Map<Object, Object> getProperties() {
        if (this.properties == null) {
            this.properties = new Properties();
        }
        return this.properties;
    }

    public <T> T getProperty(String name) {
        return (T) this.getProperties().get(name);
    }

    public EmailAccount setProperty(String name, Object value) {
        this.getProperties().put(name, value);
        return this;
    }

    /******************** getter and setter end **************************************************/

    public Email newEmail() {
        return Email.of(this);
    }

    public EmailInbox inbox() {
        return EmailInbox.of(this);
    }

    private final static String MAIL_HOST_KEY = "mail.smtp.host";
    private final static String MAIL_AUTH_key = "mail.smtp.auth";
    private final static String MAIL_TIMEOUT_KEY = "mail.smtp.timeout";
    private final static String MAIL_SSL_ENABLE_KEY = "mail.smtp.ssl.enable";
    private final static String MAIL_SSL_SOCKET_KEY = "mail.smtp.ssl.socketFactory";

    private Properties getProps() {
        requireNonNull(getHost());

        if (this.isSSLEncrypt()) {
            try {
                MailSSLSocketFactory sf = new MailSSLSocketFactory();
                sf.setTrustAllHosts(true);
                properties.put(MAIL_SSL_ENABLE_KEY, "true");
                properties.put(MAIL_SSL_SOCKET_KEY, sf);
            } catch (GeneralSecurityException e) {
                throw new IllegalArgumentException(e);
            }
        }

        return properties;
    }

    public Session getSession() {
        Properties props = getProps();
        if (getUsername() != null && getPassword() != null) {
            props.put(MAIL_AUTH_key, true);

            Authenticator authenticator = new EmailAccountAuthentication(getUsername(), getPassword());

            return Session.getDefaultInstance(props, authenticator);
        }
        return Session.getDefaultInstance(props);
    }

    private static class EmailAccountAuthentication extends Authenticator {
        private String username;
        private String password;

        public EmailAccountAuthentication(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }

    /******************** inner tools ***********************************************************/

    private <T> T requireNonNull(T o) {
        if (o == null) {
            throw new NullPointerException();
        }
        return o;
    }

    @Override
    public EmailAccount clone() {
        return new EmailAccount(
            this.getHost(),
            this.getUsername(),
            this.getPassword(),
            this.getTimeout(),
            this.isSSLEncrypt());
    }
}
