package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private RedisTemplate redisTemplate;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")      // @Value 注解会将 application.properties 配置文件中的指定值注入给它
    private String domain;                  // 域名

    @Value("${server.servlet.context-path}")
    private String contextPath;             // 项目名

    public User findUserById(int id){
 //       return userMapper.selectById(id);
        User user = getCache(id);
        if(user == null){
            user = initCache(id);
        }
        return user;
    }

    // 注册方法：注册需要 username、password、email，所以传入一个 user
    // 返回的应该是相关信息，比如“账号已存在、邮箱不能为空”等等，所以为了封装多个内容，返回Map
    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();

        // 先进行空值处理 user 为 null
        // （username为null、password为null、email为null 或者 全部为 null）

        // 空值处理
        if(user == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        // 账号/密码/邮箱 为空是业务上的漏洞，但是不是程序的错误，因此我们把信息封装到map里面返回给客户端
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        // 验证账号是否已存在
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }

        // 验证邮箱是否已被注册
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }

        // 如果可以执行到这里说明账号/密码/邮箱都不为空，且账号/邮箱都未注册

        // 这里我们要对用户输入的密码后面加一个salt（随机字符串）（password+salt 之后md5加密之后才真正存到数据库的）
        // salt 一般 5 位就够了
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);            // 0 表示普通用户
        user.setStatus(0);          // 0 表示没有激活
        user.setActivationCode(CommunityUtil.generateUUID());   //  设置激活码
        // 图片url：https://images.nowcoder.com/head/0t.png   注：从 0 - 1000 都是图片
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000))); //设置图片的路径
        user.setCreateTime(new Date());     // 设置注册时间

        userMapper.insertUser(user);        // 存入数据库，本来user的id是null，直行这句之后mysql生成id赋值给了它

        // 给用户发送 html 激活邮件，好带有链接
        // 给用户发送发送邮件

        // 给 themeleaf 模板传参
        Context context = new Context();       // themeleaf 包下的 Context
        context.setVariable("email", user.getEmail());


        // 项目路径下某个功能哪个用户在激活激活码是什么
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();

        // 由于拼接url可能会造成空格问题，空格在浏览器地址栏中会解析成 %20 ，造成错误，所以我们要将url中的空格去掉
        url = url.replaceAll(" ", "");
        context.setVariable("url", url);


        // 调模板引擎生成动态网页   参数1：模板引擎的路径   参数2：数据
        // 会生成一个动态网页，其实就是一个字符串，模板引擎主要的作用就是生成动态网页
        String content = templateEngine.process("/mail/activation", context);

        // 发邮件    参数1：收件人    参数2：邮件标题      参数3：邮件内容
        System.out.println(user.getEmail());

        try {
            mailClient.sendMail(user.getEmail(), "激活账号", content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 最后没有问题的话也返回map，且这里map是空的

        return map;
    }

    // 激活方法   参数1：用户id      参数2：激活码
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);

        if(user.getStatus() == 1){
            // 已经激活过了，说明这次是重复激活的。
            return ACTIVATION_REPEAT;      // 返回重复激活的激活码
        } else if(user.getActivationCode().equals(code)){
            // 还没有激活，且激活码正确，那么激活，并返回激活成功的激活码
            userMapper.updateStatus(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            // 激活失败返回激活失败的激活码
            return ACTIVATION_FAILURE;
        }
    }

    // 参数1：用户名  参数2：密码  参数3：过期的秒数
    public Map<String, Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        // 验证账号
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg", "该账号不存在!");
            return map;
        }
        // 验证状态
        if(user.getStatus() == 0){
            map.put("usernameMsg", "该账号未激活!");
            return map;
        }
        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg", "密码不正确!");
            return map;
        }
        // 如果上面的都正确说明 账号和密码 都正确
        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);       // 0 表示当前凭证有效
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000)); // *1000 是从毫秒换算成秒
//        loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket); // redis会把loginTicket序列化为一个json字符串

        map.put("ticket", loginTicket.getTicket());  // 返回登录凭证给客户端
        return map;
    }

    public void logout(String ticket){
        //loginTicketMapper.updateStatus(ticket, 1);      // 改变凭证状态为1，表示凭证无效
        // 将凭证取出来状态改为1表示删除态，然后再存进去
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket  = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);       // 表示删除态
        redisTemplate.opsForValue().set(redisKey, loginTicket); // 新存的值会覆盖原有的值
    }

    public LoginTicket findLoginTicket(String ticket){
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        //return  loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId, String headerUrl){
//        return userMapper.updateHeader(userId, headerUrl);
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }


    // 1.优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    // 2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    // 3.数据变更时清除缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findUserById(userId);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }

    // 重置密码
    public Map<String, Object> resetPassword(String email, String password) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(email)) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 验证邮箱
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            map.put("emailMsg", "该邮箱尚未注册!");
            return map;
        }

        // 重置密码
        password = CommunityUtil.md5(password + user.getSalt());
        userMapper.updatePassword(user.getId(), password);
        clearCache(user.getId());

        map.put("user", user);
        return map;
    }
}
