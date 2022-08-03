package com.nowcoder.community.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussRepository;    // 往ES里存、修改、删除数据、搜索可以用到

    @Autowired
    private ElasticsearchTemplate elasticTemplate;      // 这个的搜索方法可以做到高亮显示

    // 往ES里存数据(再存一次就是修改)
    public void saveDiscussPost(DiscussPost post) {
        discussRepository.save(post);
    }

    // 从ES里删除数据
    public void deleteDiscussPost(int id) {
        discussRepository.deleteById(id);
    }

    // 提供搜索方法并高亮显示  参数1:搜索的关键字, 搜索支持分页,传入分页条件 参数2:当前要显示第几页 参数3:每页显示多少条数据
    // Page是Spring提供的，不是我们自己写的实体类
    public Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))//搜索的关键词并且在哪个字段搜
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))       // 排序方式：倒序
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))      // 排序方式：倒序
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC)) // 排序方式：倒序
                .withPageable(PageRequest.of(current, limit))                         // 分页方式
                .withHighlightFields(    // 指定哪些字段要高亮显示，怎么高亮显示
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),  // 高亮显示
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>") // 高亮显示
                ).build();
        // 参数1：搜索条件      参数2：实体类型    参数3：SearchResultMapper接口(实现一个匿名内部类或者传一个实现类)
        return elasticTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override               // queryForPage得到结果然后交给mapResults处理，然后通过SearchResponse参数处理
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                SearchHits hits = response.getHits();           // 先取到这次搜索命令的数据(里面可以是多条数据)
                if (hits.getTotalHits() <= 0) {                 // 判断有没有数据
                    return null;
                }

                List<DiscussPost> list = new ArrayList<>();
                for (SearchHit hit : hits) {                    // 遍历命中的数据将其放在集合里
                    DiscussPost post = new DiscussPost();       // 将命中的数据包装到实体类中
                    // hit里面是将数据封装成了map并且里面key和value都是String类型，我们可以从中取值
                    String id = hit.getSourceAsMap().get("id").toString();
                    post.setId(Integer.valueOf(id));            // 将字符类型的数转成整数存入实体类的id属性

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
    }
}
