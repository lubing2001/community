package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTests {

    @Autowired
    private DiscussPostMapper discussMapper;    // 先从mysql取出数据

    @Autowired
    private DiscussPostRepository discussRepository;   // 注入刚才的那个接口以便于将数据存到es查询

    @Autowired
    private ElasticsearchTemplate elasticTemplate;    // 有些情况DiscussPostRepository解决不了就用这个

    @Test
    public void testInsert() {
        // 插入数据
        discussRepository.save(discussMapper.selectDiscussPostById(241));
        discussRepository.save(discussMapper.selectDiscussPostById(242));
        discussRepository.save(discussMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertList() {
        discussRepository.saveAll(discussMapper.selectDiscussPosts(101, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(102, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(103, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(111, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(112, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(131, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(132, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(133, 0, 100, 0));
        discussRepository.saveAll(discussMapper.selectDiscussPosts(134, 0, 100, 0));
    }

    @Test
    public void testUpdate() {
        DiscussPost post = discussMapper.selectDiscussPostById(231);
        post.setContent("我是新人,使劲灌水.");
        discussRepository.save(post);
    }

    @Test
    public void testDelete() {
         //discussRepository.deleteById(231);
        discussRepository.deleteAll();
    }
    // 搜索功能
    @Test
    public void testSearchByRepository() {
        // 构造搜索条件：要不要排序、分页并且搜索结果要不要高亮显示等
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))//搜索的关键词并且在哪个字段搜
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))        // 排序方式：倒序
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))       // 排序方式：倒序
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))  // 排序方式：倒序
                .withPageable(PageRequest.of(0, 10))                        // 分页方式
                .withHighlightFields(       // 指定哪些字段要高亮显示，怎么高亮显示
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),  // 高亮显示
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>") // 高亮显示
                ).build();

        // elasticTemplate.queryForPage(searchQuery, class, SearchResultMapper)
        // 底层获取得到了高亮显示的值, 但是没有返回.

        // 这个Page不是我们自己写的那个实体类，而是java提供的
        Page<DiscussPost> page = discussRepository.search(searchQuery);
        System.out.println(page.getTotalElements());        // 一共有多少条数据匹配
        System.out.println(page.getTotalPages());           // 一共有多少页
        System.out.println(page.getNumber());               // 当前处在第几页
        System.out.println(page.getSize());                 // 每一页最多显示几条数据
        for (DiscussPost post : page) {                     // 查看查询到的数据
            System.out.println(post);
        }
    }

    @Test
    public void testSearchByTemplate() {
        // 构造搜索条件：要不要排序、分页并且搜索结果要不要高亮显示等
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))//搜索的关键词并且在哪个字段搜
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))        // 排序方式：倒序
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))       // 排序方式：倒序
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))  // 排序方式：倒序
                .withPageable(PageRequest.of(0, 10))                        // 分页方式
                .withHighlightFields(       // 指定哪些字段要高亮显示，怎么高亮显示
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),  // 高亮显示
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>") // 高亮显示
                ).build();
        // 参数1：搜索条件      参数2：实体类型    参数3：SearchResultMapper接口(实现一个匿名内部类或者传一个实现类)
        Page<DiscussPost> page = elasticTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override            // queryForPage得到结果然后交给mapResults处理，然后通过SearchResponse参数处理
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                SearchHits hits = response.getHits();   // 先取到这次搜索命令的数据(里面可以是多条数据)
                if (hits.getTotalHits() <= 0) {         // 判断有没有数据
                    return null;
                }
                // 执行到这里说明有数据
                List<DiscussPost> list = new ArrayList<>();
                for (SearchHit hit : hits) {        // 遍历命中的数据将其放在集合里
                    DiscussPost post = new DiscussPost();   // 将命中的数据包装到实体类中
                    // hit里面是将数据封装成了map并且里面key和value都是String类型，我们可以从中取值
                    String id = hit.getSourceAsMap().get("id").toString();
                    post.setId(Integer.valueOf(id)); // 将字符类型的数转成整数存入实体类的id属性

                    String userId = hit.getSourceAsMap().get("userId").toString();
                    post.setUserId(Integer.valueOf(userId));

                    String title = hit.getSourceAsMap().get("title").toString();
                    post.setTitle(title);

                    String content = hit.getSourceAsMap().get("content").toString();
                    post.setContent(content);

                    String status = hit.getSourceAsMap().get("status").toString();
                    post.setStatus(Integer.valueOf(status));

                    String createTime = hit.getSourceAsMap().get("createTime").toString();
                    post.setCreateTime(new Date(Long.valueOf(createTime)));

                    String commentCount = hit.getSourceAsMap().get("commentCount").toString();
                    post.setCommentCount(Integer.valueOf(commentCount));

                    // 处理高亮显示的结果
                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if (titleField != null) {
                        // getFragments()返回的是一个数组，因为匹配的词条有可能是多个，我们只将第一个设置成高亮即可
                        post.setTitle(titleField.getFragments()[0].toString());
                    }

                    HighlightField contentField = hit.getHighlightFields().get("content");
                    if (contentField != null) {
                        post.setContent(contentField.getFragments()[0].toString());
                    }

                    list.add(post);
                }
                // AggregatedPageImpl   参数1：集合 参数2：方法参数pageable 参数3：一共多少条数据
                //                      参数4：     参数5：                 参数6：
                return new AggregatedPageImpl(list, pageable,
                        hits.getTotalHits(), response.getAggregations(), response.getScrollId(), hits.getMaxScore());
            }
        });

        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }
}
