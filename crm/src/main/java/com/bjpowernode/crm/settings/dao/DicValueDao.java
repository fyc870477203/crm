package com.bjpowernode.crm.settings.dao;

import com.bjpowernode.crm.settings.domain.DicValue;

import java.util.List;

/**
 * 作者:fyc
 * 2019/8/29
 */
public interface DicValueDao {


    List<DicValue> getValueListByCode(String code);
}
