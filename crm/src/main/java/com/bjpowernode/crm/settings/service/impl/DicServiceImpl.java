package com.bjpowernode.crm.settings.service.impl;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.dao.DicTypeDao;
import com.bjpowernode.crm.settings.dao.DicValueDao;
import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.DicType;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.DicService;
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
public class DicServiceImpl implements DicService {
    private DicTypeDao  dicTypeDao= SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private DicValueDao dicValueDao= SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);
    @Override
    public Map<String, List<DicValue>> getAll() {
        Map<String,List<DicValue>> map = new HashMap<>();
        //取得字典类型列表
        List<DicType> dtList = dicTypeDao.getTypeList();
        //遍历字典类型列表
        for(DicType dt:dtList){
            //取得每一个字典类型编码code
            String code = dt.getCode();

            //根据每一个code查询字典值列表

            List<DicValue> dvList = dicValueDao.getValueListByCode(code);
            map.put(code+"List", dvList);
        }
        return map;
    }
}
