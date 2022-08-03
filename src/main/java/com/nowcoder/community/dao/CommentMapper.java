package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.ManagedBean;
import java.util.List;

@Mapper
public interface CommentMapper {

    /*
     分页查询评论
     参数：1.评论的类型  2.评论的是哪个评论的id  3.起始页  4.每页限制条数
     */
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    /*
     查询评论的总条数
     参数：1.评论的类型  2.评论的是哪个评论的id
     */
    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);
    /**
     * 插入一条评论
     */

    Comment selectCommentById(int id);
    /**
     * 根据评论id查询评论
     */

}
