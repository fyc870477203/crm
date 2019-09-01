package com.bjpowernode.crm.workbench.dao;

/**
 * 作者:fyc
 * 2019/8/30
 */
public interface ActivityRemarkDao {
    int getCountByAids(String[] ids);

    int deleteByAids(String[] ids);
}
