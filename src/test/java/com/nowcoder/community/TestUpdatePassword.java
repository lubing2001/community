package com.nowcoder.community;

import com.nowcoder.community.controller.HomeController;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class TestUpdatePassword {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HomeController homeController;

    @Test
    public void testPassword(){
        User user = userMapper.selectByName("xixi");
        System.out.println(user.getPassword());
        System.out.println(CommunityUtil.md5("123" + user.getSalt()));
        System.out.println(user.getPassword().equals(CommunityUtil.md5("123" + user.getSalt())));
    }

    @Test
    public void testUpdatePassWord(){


    }
}
