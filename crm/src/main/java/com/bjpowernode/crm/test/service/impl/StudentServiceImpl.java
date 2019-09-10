package com.bjpowernode.crm.test.service.impl;

import com.bjpowernode.crm.test.dao.StudentDao;
import com.bjpowernode.crm.test.domain.Student;
import com.bjpowernode.crm.test.service.StudentService;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVo;

import java.util.List;
import java.util.Map;

/**
 * 作者:fyc
 * 2019/9/7
 */
public class StudentServiceImpl implements StudentService {
    private StudentDao studentDao = SqlSessionUtil.getSqlSession().getMapper(StudentDao.class);


    @Override
    public PaginationVo<Student> pageList(Map<String, Object> map) {
        int total = studentDao.getTotal(map);
        List<Student> dataList = studentDao.pageList(map);
        PaginationVo<Student> vo = new PaginationVo<>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        return vo;
    }
}
