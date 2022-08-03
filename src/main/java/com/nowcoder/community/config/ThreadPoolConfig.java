package com.nowcoder.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling                   // 启用定时任务
@EnableAsync                        // 加上这个注解可以使用简便方式调用
public class ThreadPoolConfig {
}
