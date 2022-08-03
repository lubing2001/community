package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration   // 这个注解表明这个类是一个注解类，而不是普通的类
public class AlphaConfig {

    @Bean          // 这个Bean的名字就是方法的名字
    public SimpleDateFormat simpleDateFormat(){
        return  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //返回一个实例并指定日期格式
    }
}
