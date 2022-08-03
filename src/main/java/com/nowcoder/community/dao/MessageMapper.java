package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 开发私信列表页面：查询当前用户的会话列表（支持分页），针对每个会话只返回一条最新的消息
    List<Message> selectConversations(int userId, int offset, int limit);

    // 开发私信列表页面：查询当前用户的会话数量
    int selectConversationCount(int userId);

    // 开发私信详情页面：查询某个会话包含的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 开发私信详情页面：查询某个会话所包含的私信数量
    int selectLetterCount(String conversationId);

    // 查询未读私信的数量， 这里conversationId作为动态的条件去拼，不是一定会有，传了就拼上去，这样这个方法能够实现两种业务
    int selectLetterUnreadCount(int userId, String conversationId);

    // 新增消息(私信)
    int insertMessage(Message message);

    // 修改私信的状态(未读-->已读或添加私信后首先直接设置为未读货值设置为删除)
    int updateStatus(List<Integer> ids, int status);

    // 查询某个主题下最新的通知
    Message selectLatestNotice(int userId, String topic);

    // 查询某个主题所包含的通知的数量
    int selectNoticeCount(int userId, String topic);

    // 查询未读的通知的数量
    int selectNoticeUnreadCount(int userId, String topic);

    // 查询某个主题所包含的通知列表
    List<Message> selectNotices(int userId, String topic, int offset, int limit);
}
