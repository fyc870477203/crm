package com.bjpowernode.crm.web.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * 作者:fyc
 * 2019/8/30
 */
public class EncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest res, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        System.out.println("进入过滤字符编码的操作");
        res.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        chain.doFilter(res, resp);
    }
}
