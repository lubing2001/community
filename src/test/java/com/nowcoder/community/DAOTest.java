package com.nowcoder.community;

import com.nowcoder.community.dao.CommentMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class DAOTest {

    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void testCommentMapper(){
        commentMapper.selectCommentsByEntity(1, 228, 0, 10).forEach( comment -> System.out.println(comment));

        System.out.println(commentMapper.selectCountByEntity(1, 228));
    }
}
