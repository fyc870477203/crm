package com.bjpowernode.crm.test.dao;

import com.bjpowernode.crm.test.domain.Student;

import java.util.List;
import java.util.Map;

/**
 * 作者:fyc
 * 2019/9/7
 */
public interface StudentDao {


    int getTotal(Map<String, Object> map);

    List<Student> pageList(Map<String, Object> map);
}
