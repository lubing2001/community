package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;      // 用于查询

    @Autowired
    private UserService userService;                        // 搜到帖子以后还要展现作者

    @Autowired
    private LikeService likeService;                        // 搜到帖子以后还要展现帖子点赞的数量

    // search?keyword=xxx
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    //                                  这里的Page是我们自己写的实体类
    //  参数1：搜索的关键字    参数2：传入分页的条件(我们封装的Page接收)   参数3：用于向模板传数据
    public String search(String keyword, Page page, Model model) {
        // 搜索帖子
        // 因为和实体类冲突了，所以会自动带上包名，泛型里写DiscussPost
        org.springframework.data.domain.Page<DiscussPost> searchResult =
                // 参数1：关键词          参数2：当前是第几页(方法要求从0开始)        参数3：每页显示多少条
                elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        // 聚合数据(用户名、帖子点赞数量)
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchResult != null) {
            for (DiscussPost post : searchResult) {
                Map<String, Object> map = new HashMap<>();
                // 帖子
                map.put("post", post);
                // 作者
                map.put("user", userService.findUserById(post.getUserId()));
                // 点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        // 分页信息
        page.setPath("/search?keyword=" + keyword);         // 设置路径
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements()); // 总共多少条数据

        return "/site/search";
    }

}
