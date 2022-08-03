package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)         // 表示自定义的这个注解应该写在方法之上
@Retention(RetentionPolicy.RUNTIME) // 表示这个注解在程序运行的时候有效
public @interface LoginRequired {

}
