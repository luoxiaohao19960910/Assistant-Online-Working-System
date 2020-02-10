package com.jzy.manager.util;

import org.apache.commons.lang3.StringUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

/**
 * @ClassName SendEmailUtils
 * @Author JinZhiyun
 * @Description 发送邮件工具类
 * @Date 2019/4/8 14:39
 * @Version 1.0
 **/
public class SendEmailUtils {
    private SendEmailUtils() {
    }

    public static final String FROM = "929703621@qq.com"; // 谁发送

    // 发送者的用户名和密码(邮箱登录用)
    private static final String USERNAME = "929703621"; // 此处填写发送的邮箱名
    private static final String PASSWORD = "bpofvasqxhjlbchg"; // 此处填写登录的邮箱密码（授权码！非密码）

    private static String subject = "AOWS验证码"; //邮件默认主题

    private static final String HOST = "smtp.qq.com"; //邮件服务器

    @Deprecated
    public static boolean sendMail(String emailAddress, String emailMsg) {
        return sendMail(emailAddress, emailMsg, subject);
    }

    /**
     * 向指定邮箱发送邮件
     *
     * @param emailAddressTo 目标邮箱
     * @param emailMsg 邮件内容
     * @param emailSubject 邮件主题
     * @return
     */
    @Deprecated
    public static boolean sendMail(String emailAddressTo, String emailMsg, String emailSubject) {
        // 发给谁
        String to = emailAddressTo;

        // 定义properties对象，设置环境信息
        Properties properties = new Properties();

        /*
         * mail.smtp.HOST ：指定连接的邮件服务器的主机名。如：163邮箱就填写smtp.163.com
         * 若在本地测试的话，需要在本地安装smtp服务器
         */
        properties.setProperty("mail.smtp.HOST", HOST);
        // mail.smtp.auth：指定客户端是否要向邮件服务器提交验证
        properties.setProperty("mail.smtp.auth", "true");

        /*
         * mail.transport.protocol：指定邮件发送协议：smtp。smtp：发邮件；pop3：收邮件
         * mail.store.protocol:指定邮件接收协议
         */
        properties.setProperty("mail.transport.protocol", "smtp");

        // 获取session对象
        Session session = Session.getInstance(properties);

        // 当设置为true，JavaMail AP就会将其运行过程和邮件服务器的交互命令信息输出到console中，用于JavaMail的调试
        session.setDebug(true);

        try {
            // 创建邮件对象
            MimeMessage message = new MimeMessage(session);

            // 设置邮件发送方
            message.setFrom(new InternetAddress(FROM));

            // 设置邮件发送的主题<邮件标题>
            message.setSubject(emailSubject);

            // 设置邮件发送的内容
            message.setContent(emailMsg, "text/html;charset=utf-8");

            Transport transport = session.getTransport();

            // 连接邮件服务器，“”中填写邮件服务器主机名
            transport.connect(HOST, 25, USERNAME, PASSWORD);
            transport.sendMessage(message, new Address[]{new InternetAddress(to)});
            transport.close();
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void sendEncryptedEmail(String emailAddressTo, String emailMessage) {
        sendEncryptedEmail(emailAddressTo, subject, emailMessage);
    }

    /**
     * 使用加密的方式,利用465端口进行传输邮件,开启ssl
     *
     * @param emailAddressTo 收件人邮箱
     * @param emailSubject 发送的邮件主题
     * @param emailMessage 发送的邮件内容
     */
    public static void sendEncryptedEmail(String emailAddressTo, String emailSubject, String emailMessage) {
        try {
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
            //设置邮件会话参数
            Properties props = new Properties();
            //邮箱的发送服务器地址
            props.setProperty("mail.smtp.host", HOST);
            props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            //邮箱发送服务器端口,这里设置为465端口
            props.setProperty("mail.smtp.port", "465");
            props.setProperty("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.auth", "true");
//            final String username = from;
//            final String password = psw;
            //获取到邮箱会话,利用匿名内部类的方式,将发送者邮箱用户名和密码授权给jvm
            Session session = Session.getDefaultInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            });
            //通过会话,得到一个邮件,用于发送
            Message msg = new MimeMessage(session);
            //设置发件人
            msg.setFrom(new InternetAddress(FROM));
            //设置收件人,to为收件人,cc为抄送,bcc为密送
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddressTo, false));
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailAddressTo, false));
            msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(emailAddressTo, false));
            msg.setSubject(emailSubject);
            //设置邮件消息
            msg.setText(emailMessage);
            //设置发送的日期
            msg.setSentDate(new Date());

            //调用Transport的send方法去发送邮件
            Transport.send(msg);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建发送加密邮件线程
     *
     * @param addressTo
     * @param subject
     * @param message
     * @return
     */
    private static Thread createSendEncryptedEmailThread(String addressTo, String subject, String message) {
        return new SendEncryptedEmailThread(addressTo, subject, message);
    }

    /**
     * 以线程的方式发送加密邮件
     *
     * @param addressTo
     * @param subject
     * @param message
     */
    public static void sendConcurrentEncryptedEmail(String addressTo, String subject, String message) {
        createSendEncryptedEmailThread(addressTo, subject, message).start();
    }

    /**
     * 以线程的方式发送加密邮件
     *
     * @param addressTo
     * @param message
     */
    public static void sendConcurrentEncryptedEmail(String addressTo, String message) {
        createSendEncryptedEmailThread(addressTo, subject, message).start();
    }

    /**
     * 发送加密邮件线程
     */
    private static class SendEncryptedEmailThread extends Thread {
        private String addressTo;

        private String subject;

        private String message;

        SendEncryptedEmailThread(String addressTo, String subject, String message) {
            this.addressTo = addressTo;
            this.subject = subject;
            this.message = message;
        }

        @Override
        public void run() {
            if (StringUtils.isNotEmpty(addressTo)) {
                SendEmailUtils.sendEncryptedEmail(addressTo, subject, message);
            }
        }

    }

    /**
     * 创建发送普通邮件线程
     *
     * @param addressTo
     * @param subject
     * @param message
     * @return
     */
    private static Thread createSendEmailThread(String addressTo, String subject, String message) {
        return new SendEmailThread(addressTo, subject, message);
    }

    /**
     * 以线程的方式发送普通邮件
     *
     * @param addressTo
     * @param subject
     * @param message
     */
    @Deprecated
    public static void sendConcurrentEmail(String addressTo, String subject, String message) {
        createSendEmailThread(addressTo, subject, message).start();
    }

    /**
     * 发送普通邮件线程
     */
    private static class SendEmailThread extends Thread {
        private String addressTo;

        private String subject;

        private String message;

        SendEmailThread(String addressTo, String subject, String message) {
            this.addressTo = addressTo;
            this.subject = subject;
            this.message = message;
        }

        @Override
        public void run() {
            if (StringUtils.isNotEmpty(addressTo)) {
                SendEmailUtils.sendMail(addressTo, subject, message);
            }
        }

    }
}