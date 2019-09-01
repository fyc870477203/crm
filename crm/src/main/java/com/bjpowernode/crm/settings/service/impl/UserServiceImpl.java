package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者:fyc
 * 2019/8/29
 */
public class UserServiceImpl implements UserService {
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {
        Map<String,String> map = new HashMap<>();
        map.put("loginAct", loginAct);
        map.put("loginPwd", loginPwd);
        User user = userDao.login(map);
        if(user == null){
            //抛出自定义异常
            throw new LoginException("账号密码错误");
        }
        //如果程序能够继续执行，说明账号密码正确
        //取出其他需要验证的信息
        String expireTime = user.getExpireTime();
        if(expireTime.compareTo(DateTimeUtil.getSysTime())<0){
            throw  new LoginException("账号已失效");
        }
        String lockState = user.getLockState();
        if("0".equals(lockState)){
            throw  new LoginException("账号已锁定");
        }
        String allowIps = user.getAllowIps();
        if(!allowIps.contains(ip)){
            throw new LoginException("ip地址受限");
        }

        return user;
    }

    @Override
    public List<User> getUserList() {
        List<User> uList = userDao.getUserList();
        return uList;
    }
}
