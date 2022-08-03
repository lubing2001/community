package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.annotation.ManagedBean;
import java.util.List;

@Mapper
public interface DiscussPostMapper {
                                            // orderMode：默认0按照时间排，传1表示按照热度排
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);      // 分页查询
    /*
    实现功能：分页查询
    参数1：userId   参数2：从第几条数据开始展示   参数3：每页展示几条数据
    这里要说一下，这个功能之一是展示首页的帖子，其实我们只需要查询所有数据然后分页就好了不需要传 userId，
    但是以后我们开发的时候有一个我的主页的功能，里面显示的都是我们自己的帖子，我们可以通过
    传 userId 展示自己发布过的所有帖子，所以为了以后开发方便，还是建议加上 userId
    具体实现展示首页帖子时 userId 传0, mapper配置文件处会进行修改，userId为0时查询所有帖子
     */

    int selectDiscussPostRows(@Param("userId") int userId);
    /*
    实现功能：查询数据库中有多少条数据
    参数1：userId
    在展示社区首页的时候，有一个页码，总共有多少页是由总数据条数和每页显示多少条数据决定的，我们可以把每页显示的
    数据条数固化下来，然后我们只需要一个方法查询一下数据库中总共有多少条数据。
    同样在查询社区首页数据时userId传入0，在mapper配置文件中处理时直接查询所有
    补充：@Param 注解用于给参数起别名，在mapper配置文件的动态sql中可以使用这个别名
         如果某个方法只有一个参数，并且在<if>里（动态sql）使用，则必须用@Param注解给这个参数起别名
     */

    int insertDiscussPost(DiscussPost discussPost);
    /*
     声明插入帖子的功能
     */

    DiscussPost selectDiscussPostById(int id);
    /*
    根据帖子id查询帖子的详细内容
     */

    int updateCommentCount(int id, int commentCount);
    /**
     * 根据帖子id更新评论数量
     */

    // 修改帖子类型
    int updateType(int id, int type);
    // 修改帖子状态
    int updateStatus(int id, int status);

    // 更新帖子分数
    int updateScore(int id, double score);
}
