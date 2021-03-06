package com.bjpowernode.crm.workbench.web.controller;




import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranService;
import com.bjpowernode.crm.workbench.service.impl.CustomerServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到交易模块控制器");
        String path = request.getServletPath();
        if("/workbench/transaction/add.do".equals(path)){
            add(request,response);
        }else if("/workbench/transaction/getCustomerNamesByName.do".equals(path)){
            getCustomerNamesByName(request,response);
        }else if("/workbench/transaction/save.do".equals(path)){
            save(request,response);
        }else if("/workbench/transaction/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/transaction/getHistoryList.do".equals(path)){
            getHistoryList(request,response);
        }else if("/workbench/transaction/changeStage.do".equals(path)){
            changeStage(request,response);
        }else if("/workbench/transaction/getCharts.do".equals(path)){
            getCharts(request,response);
        }
    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到展现交易的漏斗图");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        /*
            dataList
            total
         */
        PaginationVo<Map<String,Object>> vo = ts.getCharts();
        PrintJson.printJsonObj(response, vo);
    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到改变进程更新页面");
        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        Tran t = new Tran();
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        t.setId(id);
        t.setEditTime(editTime);
        t.setMoney(money);
        t.setEditBy(editBy);
        t.setExpectedDate(expectedDate);
        t.setStage(stage);

        ServletContext application = this.getServletContext();
        Map<String,String> pMap = (Map<String,String>)application.getAttribute("pMap");
        t.setPossibility(pMap.get(stage));
        boolean flag = ts.changeStage(t);
        Map<String,Object> map = new HashMap<>();
        map.put("success", flag);
        map.put("t", t);
        PrintJson.printJsonObj(response, map);
    }

    private void getHistoryList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到获得交易历史的列表");
        String tranId = request.getParameter("tranId");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> thList = ts.getHistoryList(tranId);
        ServletContext application = this.getServletContext();
        Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");
        for(TranHistory th :thList){
            th.setPossibility(pMap.get(th.getStage()));
        }
        PrintJson.printJsonObj(response, thList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        System.out.println("跳转到详细页");
        String id = request.getParameter("id");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran t = ts.detail(id);
        //取得阶段
        String stage = t.getStage();
        //取得阶段和可能性之间的关系
        ServletContext application = this.getServletContext();
        Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");
        String possibility = pMap.get(stage);
        t.setPossibility(possibility);

        request.setAttribute("t", t);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request, response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        System.out.println("进入到交易的添加操作");
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName"); //应该保存的是客户的id，但是我们现在只有name
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        Tran t = new Tran();
        t.setId(id);
        t.setType(type);
        t.setStage(stage);
        t.setName(name);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setCreateTime(createTime);
        t.setCreateBy(createBy);
        t.setActivityId(activityId);
        t.setSource(source);
        t.setOwner(owner);
        t.setNextContactTime(nextContactTime);
        t.setDescription(description);
        //t.setCustomerId();
        t.setContactSummary(contactSummary);
        t.setContactsId(contactsId);
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = ts.save(t,customerName);
        if(flag){
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
        }
    }

    private void getCustomerNamesByName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到根据姓名模糊查询姓名列表");
        String name = request.getParameter("name");
        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> sList = cs.getCustomerNamesByName(name);
        PrintJson.printJsonObj(response, sList);

    }

    private void add(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException  {
        System.out.println("跳转到交易添加页");
        //走后台的目的是为了取得用户信息列表
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList = us.getUserList();
        request.setAttribute("uList", uList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request, response);
    }

}
