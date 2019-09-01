package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.service.ActivityService;
import jdk.nashorn.internal.ir.Flags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者:fyc
 * 2019/8/30
 */
public class ActivityServiceImpl implements ActivityService {
    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
    @Override
    public PaginationVo<Activity> pageList(Map<String, Object> map) {
        //取得数据集合
        List<Activity> dataList =activityDao.getActivityListByCondition(map);
        //取得total
        int total = activityDao.getTotalByCondition(map);
        //添加到vo
       PaginationVo<Activity> vo = new PaginationVo<>();
       vo.setDataList(dataList);
       vo.setTotal(total);
        return vo;
    }

    @Override
    public boolean update(Activity a) {
        boolean flag = true;
        int count = activityDao.update(a);
        if(count!=1){
            flag= false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        List<User> uList = userDao.getUserList();
        Activity a = activityDao.getById(id);
        Map<String,Object> map = new HashMap<>();
        map.put("uList", uList);
        map.put("a",a);
        return map;
    }

    @Override
    public boolean delete(String[] ids) {
        boolean flag= true;
        //删除市场活动前，需要先删除市场活动关联的备注

        //得到需要删除备注的数量
        int count1 = activityRemarkDao.getCountByAids(ids);

        //删除备注，得到实际删除的数量
        int count2 = activityRemarkDao.deleteByAids(ids);
        if(count1 != count2){
            flag = false;
        }
        //删除市场活动
        int count3 = activityDao.delete(ids);

        if(count3!=ids.length){
            flag=false;
        }
        return flag;
    }

    @Override
    public boolean save(Activity a) {
        boolean flag = true;
        int count = activityDao.save(a);
        if(count != 1){
            flag = false;
        }
        return flag;
    }
}
