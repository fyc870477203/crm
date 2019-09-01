package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.MD5Util;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到用户控制器模块");
        String path=request.getServletPath();
        if("/settings/user/login.do".equals(path)){
            login(request,response);
        }else if("/settings/user/xxx.do".equals(path)){

        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行验证登录的操作");
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        loginPwd = MD5Util.getMD5(loginPwd);
        //接受一个IP地址
        String ip = request.getRemoteAddr();
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        try {
            User user = us.login(loginAct,loginPwd,ip);
            request.getSession().setAttribute("user", user);
            PrintJson.printJsonFlag(response, true);
            //登录成功
            //{"success":true}
        } catch (LoginException e) {
            e.printStackTrace();
            //取得错误信息
            String msg = e.getMessage();
            //登录失败
            //{"success":false,"msg":?}
            //需要同时为前端提供两个或者两个以上的值
            //做法1；
            //我们将这些值保存到一个map当中返回map
            //做法2：
            //我们可以创建一个VO类，将信息封装到VO对象当中返回一个vo
            Map<String,Object> map = new HashMap<>();
            map.put("success", false);
            map.put("msg", msg);
            //将错误信息返回给前端页面
            PrintJson.printJsonObj(response, map);
        }
    }

}
