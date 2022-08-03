package com.nowcoder.community;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class ServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testUserService(){
        User user = new User();
        user.setUsername("xixi");
        user.setPassword("123");
        user.setEmail("1137407906@qq.com");
        System.out.println(userService.register(user));

    }


}
