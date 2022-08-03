package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;

@Service
public class AlphaService {
    private static final Logger logger = LoggerFactory.getLogger(AlphaService.class);

    @Autowired
    private AlphaDao alphaDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    /*
    这个类是Spring自动创建的，我们无须配置，直接注入即可
    */
    @Autowired
    private TransactionTemplate transactionTemplate;

    public AlphaService(){
        //System.out.println("实例化AlphaService");
    }
    /*
    @PostConstruct
    // 让容器管理这个方法，也就是让容器在合适的时候自动的调用这个方法
    // @PostConstruct注解的意思是这个方法会在构造器之后调用，初始化方法一般都是在构造之后调用的
    public void init(){
        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    // 加上这个方法之后会在对象销毁之前调用它可以去释放一些资源
    public void destory(){
        System.out.println("销毁AlphaService");

    }
    */

    public String find(){
        return alphaDao.select();
    }

    /*
    isolation 事务的隔离级别
    propagation 事务的传播机制：
        REQUIRED: 支持当前事务(外部事务),如果不存在则创建新事务.
        REQUIRES_NEW: 创建一个新事务,并且暂停当前事务(外部事务).
        NESTED: 如果当前存在事务(外部事务),则嵌套在该事务中执行(独立的提交和回滚),不存在外部事务就会和REQUIRED一样
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        // 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 新增帖子
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());         // 虽然上面我们没有给user设置id，但是执行过数据库操作之后，数据库给user的id
        post.setTitle("Hello");
        post.setContent("新人报道!");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);

        // 报错
        Integer.valueOf("abc");         // 将 "abc" 这个字符串转换为整数，肯定转不了，报错
        return "ok";
    }
    public Object save2(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);  // 设置隔离级别
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED); // 设置传播机制

        return transactionTemplate.execute(new TransactionCallback<Object>() {

            @Override
            public Object doInTransaction(TransactionStatus status) {
                // 新增用户
                User user = new User();
                user.setUsername("beta");
                user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                user.setEmail("beta@qq.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/999t.png");
                user.setCreateTime(new Date());
                userMapper.insertUser(user);

                // 新增帖子
                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("你好");
                post.setContent("我是新人!");
                post.setCreateTime(new Date());
                discussPostMapper.insertDiscussPost(post);

                Integer.valueOf("abc");

                return "ok";
            }
        });
    }

    // Spring普通线程池，让该方法在多线程环境下,被异步的调用，和主线程异步执行，并发执行
    @Async
    public void execute1() {
        logger.debug("execute1");
    }
    // Spring定时线程池执行简化操作
    //@Scheduled(initialDelay = 10000, fixedRate = 1000)  // 延迟10000ms执行，时间间隔1000ms
    public void execute2() {
        logger.debug("execute2");
    }
}
