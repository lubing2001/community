package com.nowcoder.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

// 配置 Kaptcha 的工具类
// Kaptcha可以生成验证码图片
@Configuration
public class KaptchaConfig {

    @Bean
    public Producer kaptchaProducer(){
        // 这个类其实可以从properties配置文件里读数据，但是我们不这样做，我们直接往里面塞数据
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100");  // 设置图片宽度，单位：像素
        properties.setProperty("kaptcha.image.height", "40"); // 设置图片高度
        properties.setProperty("kaptcha.textproducer.font.size", "32"); // 设置字号为 32 号字
        properties.setProperty("kaptcha.textproducer.font.color", "black"); // 设置字的颜色
        // 设置验证码从这些字符串中挑字符拼接用
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        // 设置验证码长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 设置使用哪个干扰类（对图片造成干扰）
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");


        // Producer是个接口，DefaultKaptcha是它的实现类
        // Producer有两个方法，一个是创建图片，一个是生成验证码，但是要通过Config对象做一些配置
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        // 所有的配置都是通过config配的，config又依赖于Properties
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
