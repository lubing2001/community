package com.nowcoder.community.dao.elasticsearch;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository         // es可以被看成一个特殊的数据库
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {

}
