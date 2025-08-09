package com.wybbb.rainbobit.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailSender {

    private final JavaMailSender mailSender;

    // 从配置文件注入发件人邮箱
    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 发送纯文本邮件。
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param text 邮件内容
     */
    @Async // 异步执行此方法，避免阻塞调用者
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail); // 从配置中获取发件人
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);

            log.info("验证码成功发到: " + to);
        } catch (MailException e) {

            log.error("验证码没能发到: " + to + ".错误信息: " + e.getMessage());
        }
    }

    /**
     * 发送验证码邮件的专用方法
     * @param to 收件人邮箱
     * @param verificationCode 验证码
     */
    @Async
    public void sendVerificationCodeEmail(String to, String verificationCode) {
        String subject = "您的验证码";
        String content = "您好，您的验证码是：" + verificationCode + "，请在5分钟内使用。请勿告知他人。";
        sendSimpleEmail(to, subject, content);
    }
}