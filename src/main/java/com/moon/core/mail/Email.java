package com.moon.core.mail;

import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.ListUtil;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author moonsky
 * @see EmailUtil
 */
public class Email implements Cloneable, Serializable {

    private EmailAccount account;
    /**
     * 发件人
     */
    private String from;
    /**
     * 收件人
     */
    private List<String> toUsers;
    /**
     * 抄送人
     */
    private List<String> copies;
    /**
     * 秘送用户
     */
    private List<String> bccs;

    /**
     * 邮件标题
     */
    private String title;

    /**
     * 邮件正文
     */
    private String content;

    /**
     * 正文是否是 HTML 片段
     */
    private boolean isHtml;

    /**
     * 附件
     */
    private Map<String, File> attachments;

    private String charset;

    /******************************** 构造函数 ********************************************************/
    private Email(EmailAccount account) {
        this.setAccount(account);
    }

    public static Email of(EmailAccount account) {
        return new Email(account);
    }

    /******************************** 构造函数结束 *****************************************************/

    /**
     * 添加收件人
     *
     * @param toUserMailAddress
     *
     * @return
     */
    public Email addToUser(String toUserMailAddress) {
        getToUsers().add(toUserMailAddress);
        return this;
    }

    public Email addToUsers(String... toUsers) {
        ListUtil.addAll(getToUsers(), toUsers);
        return this;
    }

    public Email addToUsers(Collection<String> toUsers) {
        ListUtil.addAll(getToUsers(), toUsers);
        return this;
    }

    public Email removeToUser(String toUser) {
        getToUsers().remove(toUser);
        return this;
    }

    /**
     * 添加抄送人
     *
     * @param copyMailAddress
     *
     * @return
     */
    public Email addCopy(String copyMailAddress) {
        getToUsers().add(copyMailAddress);
        return this;
    }

    public Email addCopies(String... copiesMailAddresses) {
        ListUtil.addAll(getCopies(), copiesMailAddresses);
        return this;
    }

    public Email addCopies(Collection<String> copiesMailAddresses) {
        ListUtil.addAll(getCopies(), copiesMailAddresses);
        return this;
    }

    public Email removeCopy(String copy) {
        getCopies().remove(copy);
        return this;
    }

    /**
     * 添加秘送人
     *
     * @param bccAddress
     *
     * @return
     */
    public Email addBcc(String bccAddress) {
        getBccs().add(bccAddress);
        return this;
    }

    public Email addBccs(String... bccAddress) {
        ListUtil.addAll(getBccs(), bccAddress);
        return this;
    }

    public Email addBccs(List<String> bccAddress) {
        ListUtil.addAll(getBccs(), bccAddress);
        return this;
    }

    public Email removeBcc(String bcc) {
        getBccs().remove(bcc);
        return this;
    }

    /**
     * 添加附件 : 调用不带附件名的方法时默认文件名为附件名
     *
     * @param attachment
     *
     * @return
     */
    public Email addAttachment(File attachment) {
        return this.addAttachment(attachment.getName(), attachment);
    }

    public Email addAttachmentByPath(String attachmentPath) {
        return this.addAttachments(new File(attachmentPath));
    }

    public Email addAttachment(String attachmentName, File attachment) {
        this.getAttachments().put(attachmentName, attachment);
        return this;
    }

    public Email addAttachmentByPath(String attachmentName, String attachmentPath) {
        return this.addAttachment(attachmentName, new File(attachmentPath));
    }

    public Email addAttachments(File... attachments) {
        if (attachments.length > 0) {
            for (File file : attachments) {
                addAttachment(file);
            }
        }
        return this;
    }

    public Email addAttachmentsByPath(String... attachmentsPath) {
        if (attachmentsPath.length > 0) {
            for (String filePath : attachmentsPath) {
                addAttachment(new File(filePath));
            }
        }
        return this;
    }

    public Email addAttachments(List<File> attachments) {
        if (isNotEmpty(attachments)) {
            for (File file : attachments) {
                addAttachment(file);
            }
        }
        return this;
    }

    public Email addAttachmentsByPath(List<String> attachments) {
        if (isNotEmpty(attachments)) {
            for (String file : attachments) {
                addAttachment(new File(file));
            }
        }
        return this;
    }

    public Email removeAttachment(File attachment) {
        if (attachment != null) {
            getAttachments().remove(attachment.getName(), attachment);
        }
        return this;
    }

    public Email removeAttachmentByPath(String attachmentPath) {
        return removeAttachment(new File(attachmentPath));
    }

    public Email removeAttachmentByName(String attachmentName) {
        getAttachments().remove(attachmentName);
        return this;
    }

    /******************************** getter and setter ****************************************/

    // mail account
    public Email setAccount(EmailAccount account) {
        this.account = account;
        return this;
    }

    public EmailAccount getAccount() {
        return this.account;
    }

    // from
    public String getFrom() {
        return from;
    }

    public Email setFrom(String from) {
        this.from = from;
        return this;
    }

    // to users
    public List<String> getToUsers() {
        if (toUsers == null) {
            return toUsers = new ArrayList<>();
        }
        return toUsers;
    }

    public Email toUser(String toUser) {
        if (isNotEmpty(toUser)) {
            List<String> toUsers = new ArrayList<>();
            toUsers.add(toUser);
            return this.toUsers(toUsers);
        }
        return this;
    }

    public Email toUsers(String... toUsers) {
        this.toUsers = ListUtil.addAll(new ArrayList<>(), toUsers);
        return this;
    }

    public Email toUsers(List<String> toUsers) {
        this.toUsers = requireNotEmpty(toUsers);
        return this;
    }

    // copies
    public List<String> getCopies() {
        if (copies == null) {
            return copies = new ArrayList<>();
        }
        return copies;
    }

    public Email setCopy(String copyUser) {
        if (isNotEmpty(copyUser)) {
            List<String> toUsers = new ArrayList<>();
            toUsers.add(copyUser);
            return this.toUsers(toUsers);
        }
        return this;
    }

    public Email setCopies(List<String> copies) {
        this.copies = isNotEmpty(copies) ? copies : null;
        return this;
    }

    public Email setCopies(String... copies) {
        this.copies = copies.length > 0 ? ListUtil.addAll(new ArrayList<>(), copies) : null;
        return this;
    }

    // attachments : 调用不带附件名的方法时默认文件名为附件名

    public Email setAttachment(File attachment) {
        if (attachment != null) {
            Map<String, File> attachments = new HashMap<>();
            attachments.put(attachment.getName(), attachment);
            this.attachments = attachments;
        }
        return this;
    }

    public Email setAttachmentByPath(String attachmentPath) {
        if (isNotEmpty(attachmentPath)) {
            this.setAttachment(new File(attachmentPath));
        }
        return this;
    }

    public Email setAttachment(String attachmentName, File attachment) {
        requireNotEmpty(attachmentName);
        requireNonNull(attachment);
        if (attachment != null) {
            Map<String, File> attachments = new HashMap<>();
            attachments.put(attachmentName, attachment);
            this.attachments = attachments;
        }
        return this;
    }

    public Email setAttachmentByPath(String attachmentName, String attachmentPath) {
        if (isNotEmpty(attachmentPath)) {
            this.setAttachment(attachmentName, new File(attachmentPath));
        }
        return this;
    }

    public Email setAttachments(File... attachments) {
        if (attachments.length > 0) {
            this.attachments = new HashMap<>(attachments.length);
            for (File file : attachments) {
                this.attachments.put(file.getName(), file);
            }
        } else {
            this.attachments = null;
        }
        return this;
    }

    public Email setAttachments(List<File> attachments) {
        if (isNotEmpty(attachments)) {
            this.attachments = new HashMap<>(attachments.size());
            for (File file : attachments) {
                this.attachments.put(file.getName(), file);
            }
        } else {
            this.attachments = null;
        }
        return this;
    }

    public Email setAttachmentsByPath(List<String> attachments) {
        if (isNotEmpty(attachments)) {
            this.attachments = new HashMap<>(attachments.size());
            for (String path : attachments) {
                File file = new File(path);
                this.attachments.put(file.getName(), file);
            }
        } else {
            this.attachments = null;
        }
        return this;
    }

    public Map<String, File> getAttachments() {
        if (attachments == null) {
            return attachments = new HashMap<>();
        }
        return attachments;
    }

    public Email setAttachments(Map<String, File> attachments) {
        this.attachments = attachments;
        return this;
    }

    // bcc
    public List<String> getBccs() {
        if (bccs == null) {
            return bccs = new ArrayList<>();
        }
        return bccs;
    }

    public Email setBccs(String... bccs) {
        if (bccs.length > 0) {
            ListUtil.addAll(this.bccs = new ArrayList<>(), bccs);
        } else {
            this.bccs = null;
        }
        return this;
    }

    public Email setBccs(List<String> bccs) {
        if (isNotEmpty(bccs)) {
            ListUtil.addAll(this.bccs = new ArrayList<>(), bccs);
        } else {
            this.bccs = null;
        }
        return this;
    }

    // title
    public String title() {
        return title;
    }

    public Email title(String title) {
        this.title = title;
        return this;
    }

    // content
    public String content() {
        return content;
    }

    public Email content(String content) {
        this.content = content;
        return this;
    }

    // charset
    public String charset() {
        return charset;
    }

    public Email charset(String charset) {
        this.charset = charset;
        return this;
    }

    public Email charset(Charset charset) {
        return this.charset(charset.name());
    }

    // is html
    public boolean isHtml() {
        return isHtml;
    }

    public Email isHtml(boolean html) {
        isHtml = html;
        return this;
    }

    /******************************** sender **********************************************/

    /**
     * 发送邮件
     *
     * @return
     */
    public Future<?> send() {
        return send(false);
    }
    /**
     * 发送邮件
     *
     * @return
     */
    public Future<?> send(boolean sync) {
        try {
            return this.send0(this, null, sync);
        } catch (Exception e) {
            return ThrowUtil.unchecked(e);
        }
    }

    /**
     * 异步发送邮件
     *
     * @param service
     *
     * @return
     */
    public Future<?> send(ExecutorService service) {
        try {
            Email sender = this.clone();
            return sender.send0(sender, service, false);
        } catch (Exception e) {
            return ThrowUtil.unchecked(e);
        }
    }

    /**
     * 发送实际执行方法
     *
     * @param sender
     *
     * @throws MessagingException
     */
    private Future<?> send0(Email sender, ExecutorService service, boolean sync) throws MessagingException {
        String from = StringUtil.defaultIfEmpty(this.from, this.account.getUsername());
        Address fromAddress = new InternetAddress(requireNonNull(from));

        Session session = requireNonNull(sender.account).getSession();

        MimeMessage message = new MimeMessage(session);

        message.setFrom(fromAddress);

        addRecipientsOfToUsers(message, sender);
        addRecipientsOfCopies(message, sender);
        addRecipientsOfBCCS(message, sender);

        setSubject(message, sender);
        setMailContent(message, sender);

        return account.send(message, service, sync);
    }

    /**
     * 设置邮件内容
     *
     * @param message
     * @param sender
     *
     * @throws MessagingException
     */
    private void setMailContent(MimeMessage message, Email sender) throws MessagingException {
        if (isNotEmpty(sender.attachments)) {
            setMailContent0withAttachment(message, sender);
        } else {
            setMailContent0withText(message, sender);
        }
    }

    private static final String HTML_TYPE = "text/html";

    /**
     * 设置邮件纯文本 / html内容
     *
     * @param message
     * @param sender
     *
     * @throws MessagingException
     */
    private void setMailContent0withText(MimeMessage message, Email sender) throws MessagingException {
        if (sender.isHtml) {
            message.setContent(sender.content, HTML_TYPE);
        } else {
            message.setText(sender.content);
        }
    }

    /**
     * 设置带附件邮件内容
     *
     * @param message
     * @param sender
     *
     * @throws MessagingException
     */
    private void setMailContent0withAttachment(MimeMessage message, Email sender) throws MessagingException {
        Multipart multipart = new MimeMultipart();
        MimeBodyPart part = new MimeBodyPart();
        multipart.addBodyPart(part);
        if (sender.isHtml) {
            part.setContent(sender.content, HTML_TYPE);
        } else {
            part.setText(sender.content);
        }

        if (isNotEmpty(sender.attachments)) {
            for (Map.Entry<String, File> entry : sender.attachments.entrySet()) {
                File file = entry.getValue();
                String name = entry.getKey();

                BodyPart attachmentPart = new MimeBodyPart();

                DataSource source = new FileDataSource(file);
                DataHandler handler = new DataHandler(source);

                attachmentPart.setFileName(name);
                attachmentPart.setDataHandler(handler);

                multipart.addBodyPart(attachmentPart);
            }
        }
        message.setContent(multipart);
    }

    /**
     * 设置邮件标题
     *
     * @param message
     * @param sender
     *
     * @throws MessagingException
     */
    private void setSubject(MimeMessage message, Email sender) throws MessagingException {
        if (charset == null) {
            message.setSubject(sender.title);
        } else {
            message.setSubject(sender.title, sender.charset);
        }
    }

    /**
     * 添加收件人
     *
     * @param message
     * @param sender
     *
     * @throws MessagingException
     */
    private void addRecipientsOfToUsers(MimeMessage message, Email sender) throws MessagingException {
        if (sender.toUsers != null) {
            for (String address : sender.toUsers) {
                message.addRecipients(Message.RecipientType.CC, address);
            }
        }
    }

    /**
     * 添加抄送人
     *
     * @param message
     * @param sender
     *
     * @throws MessagingException
     */
    private void addRecipientsOfCopies(MimeMessage message, Email sender) throws MessagingException {
        if (sender.copies != null) {
            for (String address : sender.copies) {
                message.addRecipients(Message.RecipientType.CC, address);
            }
        }
    }

    /**
     * 添加密送人
     *
     * @param message
     * @param sender
     *
     * @throws MessagingException
     */
    private void addRecipientsOfBCCS(MimeMessage message, Email sender) throws MessagingException {
        if (sender.bccs != null) {
            for (String address : sender.bccs) {
                message.addRecipients(Message.RecipientType.BCC, address);
            }
        }
    }

    @Override
    public Email clone() {
        Email sender = Email.of(this.account.clone());

        sender.from = this.from;
        sender.toUsers = new ArrayList<>(requireNotEmpty(this.toUsers));

        if (isNotEmpty(this.copies)) {
            sender.copies = new ArrayList<>(this.copies);
        }
        if (isNotEmpty(this.bccs)) {
            sender.copies = new ArrayList<>(this.bccs);
        }

        sender.title = this.title;
        sender.content = this.content;
        sender.isHtml = this.isHtml;
        sender.charset = this.charset;

        if (isNotEmpty(this.attachments)) {
            sender.attachments = new HashMap<>(this.attachments.size());
            for (Map.Entry<String, File> entry : this.attachments.entrySet()) {
                File file = entry.getValue();
                sender.attachments.put(entry.getKey(), new File(file.getAbsolutePath()));
            }
        }
        return sender;
    }

    /******************************** tools *************************************************/

    private boolean isNotEmpty(Collection c) {
        return c != null && c.size() > 0;
    }

    private boolean isNotEmpty(Map c) {
        return c != null && c.size() > 0;
    }

    private boolean isNotEmpty(CharSequence cs) {
        return cs != null && cs.length() > 0;
    }

    private <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    private <T, C extends Collection<T>> C requireNotEmpty(C c) {
        if (isNotEmpty(c)) {
            return c;
        }
        throw new IllegalArgumentException("Collection is null or EMPTY, must not be EMPTY");
    }

    private CharSequence requireNotEmpty(CharSequence c) {
        if (isNotEmpty(c)) {
            return c;
        }
        throw new IllegalArgumentException("argument is null or EMPTY, must not be EMPTY");
    }
}
