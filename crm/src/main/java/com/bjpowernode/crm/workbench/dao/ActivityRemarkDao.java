package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.ActivityRemark;

import java.util.List;

/**
 * 作者:fyc
 * 2019/8/30
 */
public interface ActivityRemarkDao {
    int getCountByAids(String[] ids);

    int deleteByAids(String[] ids);

    int saveRemark(ActivityRemark ar);

    int deleteRemark(String id);

    List<ActivityRemark> getRemarkListByAid(String activityId);

    int updateRemark(ActivityRemark ar);
}
