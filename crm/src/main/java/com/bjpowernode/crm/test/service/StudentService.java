package com.bjpowernode.crm.test.service;

import com.bjpowernode.crm.test.domain.Student;
import com.bjpowernode.crm.vo.PaginationVo;

import java.util.Map;

/**
 * 作者:fyc
 * 2019/9/7
 */
public interface StudentService {


    PaginationVo<Student> pageList(Map<String, Object> map);
}
