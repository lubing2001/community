package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    // 为了记日志
    private static Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})   // 里面的参数表示处理所有异常
    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {        // 方法名无所谓
        // 记录一下异常的概括
        logger.error("服务器发生异常: " + e.getMessage());
        // 详细记录异常的栈的信息
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }
        /*
            普通的请求重定向到刚刚500的controller
            异步请求的话返回json，json字符串里面写错误信息
         */
        // 判断请求是普通请求还是异步请求
        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            // 异步请求
            response.setContentType("application/plain;charset=utf-8"); // plain表示向浏览器返回的是一个普通的字符串
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常!"));
        } else {
            // 普通请求重定向到错误页面
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
