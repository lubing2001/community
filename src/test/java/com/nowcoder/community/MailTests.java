package com.nowcoder.community;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testTextMail(){
        String to = "958691367@qq.com";
        String subject = "Test4";
        String content = "Welcome.";
        mailClient.sendMail(to, subject, content);
    }

    @Test
    public void testHtmlMail(){
        // 给 themeleaf 模板传参
        Context context = new Context();
        context.setVariable("username", "sunday");

        // 调模板引擎生成动态网页   参数1：模板引擎的路径   参数2：数据
        // 会生成一个动态网页，其实就是一个字符串，模板引擎主要的作用就是生成动态网页
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        // 发邮件    参数1：收件人    参数2：邮件标题      参数3：邮件内容
        mailClient.sendMail("958691367@qq.com", "HTML", content);
    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectAndUpdateTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("abc", 1);       // 失效改为1
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }
}
