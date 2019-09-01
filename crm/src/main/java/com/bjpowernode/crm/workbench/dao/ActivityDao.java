package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

/**
 * 作者:fyc
 * 2019/8/30
 */
public interface ActivityDao {
    int save(Activity a);

    List<Activity> getActivityListByCondition(Map<String, Object> map);

    int getTotalByCondition(Map<String, Object> map);

    int delete(String[] ids);

    Activity getById(String id);

    int update(Activity a);
}
