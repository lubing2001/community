package com.nowcoder.community.actuator;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 监控数据库，看连接是否正常
 */
@Component
@Endpoint(id = "database")      // 端点的id
public class DatabaseEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseEndpoint.class);

    @Autowired
    private DataSource dataSource;      // 连接池，Spring管理的，直接注入即可

    @ReadOperation      // 表示这个方法是通过get请求访问的
    public String checkConnection() {
        // 尝试获取一个连接，成功获取返回连接正常，失败返回连接失败
        try (
                Connection conn = dataSource.getConnection();    // 在（）里最后会自动加finally执行close关闭这个资源
        ) {
            return CommunityUtil.getJSONString(0, "获取连接成功!");   // 返回json格式字符串
        } catch (SQLException e) {
            logger.error("获取连接失败:" + e.getMessage());
            return CommunityUtil.getJSONString(1, "获取连接失败!");   // 返回json格式字符串
        }
    }

}
