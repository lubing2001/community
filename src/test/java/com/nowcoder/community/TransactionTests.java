package com.nowcoder.community;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.service.CommentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TransactionTests {

    @Autowired
    private AlphaService alphaService;

    @Autowired
    private CommentService commentService;


    @Test
    public void testSave1() {

        Comment comment = new Comment();
        comment.setId(0);
        commentService.addComment(comment);
    }

    @Test
    public void testSave2() {

    }

}
