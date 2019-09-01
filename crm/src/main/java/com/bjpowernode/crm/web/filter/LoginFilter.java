package com.bjpowernode.crm.web.filter;

import com.bjpowernode.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 作者:fyc
 * 2019/8/30
 */
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest res, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        System.out.println("进入到判断有没有登录过的过滤器");
        HttpServletRequest request = (HttpServletRequest) res;
        HttpServletResponse response = (HttpServletResponse) resp;
        String path=request.getServletPath();
        System.out.println(path);
        if("/login.jsp".equals(path) || "/settings/user/login.do".equals(path)){
            chain.doFilter(res, resp);
        }else{
            //从request对象中取session从session中取user
            //如果user对象为null说明没有登陆过
            //如果user对象不为null说明登录过
            User user= (User) request.getSession().getAttribute("user");
            if(user!=null){
                chain.doFilter(res, resp);
            }else{
                response.sendRedirect(request.getContextPath()+"/login.jsp");
            }
        }
    }
}
