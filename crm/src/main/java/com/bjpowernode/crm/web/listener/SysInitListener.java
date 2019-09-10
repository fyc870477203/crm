package com.bjpowernode.crm.web.listener;

import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

/**
 * 作者:fyc
 * 2019/9/2
 */
public class SysInitListener implements ServletContextListener {
    /*
    该方法是用来监听application域对象创建的方法
    如果application对象创建完毕，则马上执行该方法

    参数event，我们可以通过该参数取得监听的域对象
    例如我们现在监听的域对象是application
    那么我们就可以通过event来get到application对象
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("application对象创建了");
        ServletContext application=event.getServletContext();
        System.out.println("创建的application:"+application);
        System.out.println("服务器缓存处理数据字典开始");
        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());
        Map<String, List<DicValue>> map = ds.getAll();
        Set<String> set = map.keySet();
        for(String key:set){
            application.setAttribute(key, map.get(key));
        }
        System.out.println("服务器缓存处理数据字典结束");
        //处理完数据字典后解析Stage2Possibility.properties
        Map<String,String> pMap = new HashMap<>();
        //字典需要解析的properties文件的名字注意指定时不需要写后缀名
        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");
        Enumeration<String> e = rb.getKeys();
        while(e.hasMoreElements()){
            String key = e.nextElement();//stage
            String value = rb.getString(key);//possibility
            System.out.println("key:"+key);
            System.out.println("value:"+value);
            System.out.println("-------------------");
            pMap.put(key, value);
        }
        application.setAttribute("pMap", pMap);
    }
}
