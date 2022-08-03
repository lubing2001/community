package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot.";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    @RequestMapping("/http")
    //                 浏览器的请求对象                 controller响应请求的响应对象
    public void http(HttpServletRequest request, HttpServletResponse response){
        // 获取浏览器的请求数据

        System.out.println(request.getMethod());                // 获取请求方式
        System.out.println(request.getServletPath());           // 请求路径
        Enumeration<String> enumeration = request.getHeaderNames();  // 获取所有的请求行的key（请求行是key-value结构）
        // while里面的其实是请求头的数据
        while (enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        // 获取请求体当中的参数（业务数据）
        System.out.println(request.getParameter("code"));
        // 返回controller的响应数据的对象: response
        response.setContentType("text/html;charset=utf-8");     // 设置返回的相应数据的类型和编码
        PrintWriter writer = null;
        try {
            writer = response.getWriter();    // 获取响应对象的输出流去响应网页（异常try/catch处理）
            writer.write("<h1>牛客网</h1>");            // 通过writer向浏览器输出一个一级标题
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();                   // 关闭响应对象的输出流
        }
    }

    // GET请求

    //    /students?current=1&limit=20
    @RequestMapping(path = "/students", method = RequestMethod.GET)    // 制定了浏览器请求的方式只能为GET方式
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "20") int limit){
        System.out.println("current = " + current);
        System.out.println("limit = " + limit);
        return "some students";
    }

    //  /student/123
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println("id = " + id);
        return "a student";
    }

    // POST请求
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        System.out.println("name = " + name);
        System.out.println("age = " + age);
        return "success";
    }

    // 响应html数据

    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView modelAndView = new ModelAndView();
        // 传值
        modelAndView.addObject("name", "张三");
        modelAndView.addObject("age", 30);
        // 设置模板的路径和名字（模板会放到 templates 目录，这个路径不用写，）
        modelAndView.setViewName("/demo/view");       // view.html这个文件，后面的 .html 后缀省略
        return modelAndView;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model){
        // 传值
        model.addAttribute("name", "北京大学");
        model.addAttribute("age", "80");
        return "/demo/view";    // 返回 view
    }

    // 响应JSON数据
    //  Java对象 -> JSON字符串 -> JS对象

    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody       // 加上这个注解才会将返回的数据转换成JSON字符串
    public Map<String, Object> getEmp(){
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody       // 加上这个注解才会将返回的数据转换成JSON字符串
    public List<Map<String, Object>> getEmps(){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000.00);
        list.add(emp);
        emp = new HashMap<>();
        emp.put("name", "李四");
        emp.put("age", 24);
        emp.put("salary", 9000.00);
        list.add(emp);
        emp = new HashMap<>();
        emp.put("name", "王五");
        emp.put("age", 25);
        emp.put("salary", 10000.00);
        list.add(emp);
        return list;
    }

    // cookie 示例
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        // 创建cookie，必须传入参数（没有无参构造器），且必须都是字符串
        // 并且每一个cookie对象只能存一组字符串，即一个key-val
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());

        // 设置cookie生效的范围，服务器首先发送给浏览器cookie，然后浏览器给服务器发请求需要发cookie
        // 需要指定浏览器给服务器的哪些路径请求发cookie，不必每次都发，因为可能不需要，会浪费网络资源
        cookie.setPath("/community/alpha");     // 设置cookie只有在这个路径及子路径才有效

        // cookie默认发到浏览器，浏览器关掉就消失
        // 一旦给cookie设置了生存时间，它会存在硬盘里，长期有效，直到超过这个时间才会无效
        // 设置cookie的生存时间
        cookie.setMaxAge(60 * 10);      // 单位是秒

        // 将cookie放到response头里发送
        response.addCookie(cookie);

        return "set cookie";
    }
    // cookie 示例
    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    // @CookieValue("code")是获取cookie中key为"code"的value
    // cookie需要在服务器和客户端之间传，所以只能存少量，另外客户端可以识别字符串，其他java类型无法识别，所以用key-value
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }

    // session 示例
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        // session 创建不需要我们手动创建，SpringMVC会自动创建session并帮我们注入进来，所以只需声明即可
        // session 是一直存在在服务端的，所以它里面存什么数据都可以
        session.setAttribute("id", 1);
        session.setAttribute("name", "Test");
        return "set session";
    }

    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){

        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    // ajax示例
    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0, "操作成功!");
    }
}
