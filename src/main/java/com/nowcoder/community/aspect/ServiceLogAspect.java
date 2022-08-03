package com.nowcoder.community.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);
    // 切点
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut() {
    }
    // 使用前置通知记录日志
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {    // 这个参数是为了知道访问了哪个类的哪个方法
        // 用户[1.2.3.4,ip地址],在[xx时间],访问了[com.nowcoder.community.service.xxx()].
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null) {
            return;
        }
        // 得到request对象
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();        // 利用request得到ip地址
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // 时间
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s],在[%s],访问了[%s].", ip, now, target));
    }

}

