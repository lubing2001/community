package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {

    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut() {

    }
    // 前置通知
    @Before("pointcut()")
    public void before() {
        System.out.println("before");
    }
    // 后置通知
    @After("pointcut()")
    public void after() {
        System.out.println("after");
    }
    // 如果想在有了返回值以后处理一下逻辑可以使用这个注解（相当于后置通知的增强版）
    @AfterReturning("pointcut()")
    public void afterRetuning() {
        System.out.println("afterRetuning");
    }
    // 在抛异常之后置入代码
    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }
    // 环绕通知
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
        Object obj = joinPoint.proceed();    // 调目标组件的方法
        System.out.println("around after");
        return obj;                         // 目标组件方法的返回值
    }
}
