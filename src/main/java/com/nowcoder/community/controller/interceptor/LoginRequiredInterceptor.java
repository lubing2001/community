package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 先判断拦截的是不是方法，因为它不仅拦截了方法，还可能拦截了静态资源其他内容
        if(handler instanceof HandlerMethod){
            // 如果拦截的是一个方法
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            // 从方法上取注解
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            if(loginRequired != null && hostHolder.getUser() == null){
                // 不等于 null 表示这个方法是需要登录才能访问的
                // 但是 hostHolder.getUser() == null 表示没有登录
                // 这个时候应该拒绝后续的请求，强制重定向到登录页面 request.getContextPath() 表示取到应用的路径
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }


}
