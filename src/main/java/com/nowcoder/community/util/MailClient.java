package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {

    // 记录一些日志
    private Logger logger = LoggerFactory.getLogger(MailClient.class);

    // 发送邮件的核心组件，交给 Sping 容器去管理
    @Autowired
    private JavaMailSender mailSender;

    // 发邮箱的时候需要几个条件：1. 发件人  2. 收件人  3. 邮件的标题和内容
    // 我们在配置文件里配置了邮箱，所以发件人是固定的，就是配置文件里配置的发件人
    //我们把配置文件的spring.mail.username注入到这里，省的在方法里再传了
    @Value("${spring.mail.username}")
    private String from;

    // 发送邮件方法（记住：是public 修饰）
    // 参数1：收件人   参数2：邮件标题   参数3：邮件内容
    public void sendMail(String to, String subject, String content){
        try {
            // 创建邮件主体，但是这个message是空的，只是一个模板，我们还需要往里面填入内容。
            MimeMessage message = mailSender.createMimeMessage();

            // 使用帮助类构建 message 里面的内容，设置发件人、收件人、邮件标题、邮件内容
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);                  // 设置邮件发件人
            helper.setTo(to);                      // 设置邮件收件人
            helper.setSubject(subject);            // 设置邮件主题
            helper.setText(content, true);   // 设置邮件内容(加true表示也支持html文本，不加这个参数表示支持普通的文本)

            // 发送
            mailSender.send(helper.getMimeMessage());

        } catch (MessagingException e) {
            // 失败的话打印日志
            logger.error("发送邮件失败" + e.getMessage());
        }


    }


}
