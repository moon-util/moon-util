package com.moon.core.mail;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * 邮件工具类
 * 1、发送: EmailUtil.connect(host).login(user, pass).newEmail().<set or increment props()>.send()
 * |--这个工具类是对 JavaMail 的一个包装，它将发送邮件理解为一个过程，如下：
 * |--|--访问连接邮件服务器：EmailUtil.account(host); //故此处需要一个邮件服务器的 host，返回一个 EmailAccount
 * |--|--如果服务器需要登录，则调用 account.login(username, password); // 这样就成功访问登录一个邮件账户了
 * |--|--创建账户后便可以写邮件，由当前账户创建一个 Email，account.newEmail(); // 得到一个空白邮件 email
 * |--|--写入邮件标题、内容等具体信息：
 * |--|--|--标题（title）: setTitle()...
 * |--|--|--内容（content）: setContent()...
 * |--|--|--收件人（toUser）: addToUser()、addToUsers()、setToUsers()、removeToUser()...
 * |--|--|--抄送人（copy）: addCopy()、addCopies()、、setCopies()、removeCopy()...
 * |--|--|--密送人（bcc）: addBcc()、addBccs()、setBccs()、removeBcc()...
 * |--|--|--附件（attachment）: addAttachment()、addAttachments()、setAttachments()...
 * |--|--最后发送：email.send();
 * |--|--|--或者传入一个线程池异步发送：email.send(ExecuteService)
 * |--访问站点 -> 登录 -> 填写邮件内容 -> 发送
 * <p>
 * 2、发送邮件将采用异步发送
 *
 * @author ZhangDongMin
 * @see EmailAccount
 * @see Email
 */
public final class EmailUtil {

    /**
     * 不提供 Util 的实例对象，强制调用抛出错误
     */
    private EmailUtil() { noInstanceError(); }

    public static EmailAccount connect(String host) {
        return new EmailAccount(host);
    }

    public static EmailAccount login(String host, String username, String password) {
        return connect(host).login(username, password);
    }
}
