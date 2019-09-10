package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.dao.TranHistoryDao;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.TranService;

import java.util.List;
import java.util.Map;

/**
 * 作者:fyc
 * 2019/9/5
 */
public class TranServiceImpl implements TranService {
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public boolean save(Tran t, String customerName) {
        boolean flag = true;
        /*
            根据客户名称，到客户表中查询有没有这个客户，如果有的话就取其id封装到t的customerId当中
                            如果没有就新建一个客户取其id封装到t的customerId
         */
        Customer cus = customerDao.getCustomerByName(customerName);
        if(cus == null){
            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setName(customerName);
            cus.setCreateBy(t.getCreateBy());
            cus.setCreateTime(t.getCreateTime());
            cus.setOwner(t.getOwner());

            int count1 = customerDao.save(cus);
            if(count1 != 1){
                flag = false;
            }
        }
        // t中还差一个customerId，现在可以封装该属性值了
        t.setCustomerId(cus.getId());
        //添加交易
        int count2 = tranDao.save(t);
        if(count2 != 1){
            flag = false;
        }
        //添加交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setCreateBy(t.getCreateBy());
        th.setCreateTime(t.getCreateTime());
        th.setExpectedDate(t.getExpectedDate());
        th.setTranId(t.getId());
        th.setMoney(t.getMoney());
        th.setStage(t.getStage());
        int count3 = tranHistoryDao.save(th);
        if(count3 != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Tran detail(String id) {
        Tran t  = tranDao.detail(id);
        return t;
    }

    @Override
    public List<TranHistory> getHistoryList(String tranId) {
        List<TranHistory> thList = tranHistoryDao.getHistoryList(tranId);
        return thList;
    }

    @Override
    public boolean changeStage(Tran t) {
        boolean flag = true;
        int count = tranDao.changeStage(t);
        if(count != 1){
            flag = false;
        }
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setCreateBy(t.getEditBy());
        th.setCreateTime(t.getEditTime());
        th.setExpectedDate(t.getExpectedDate());
        th.setTranId(t.getId());
        th.setMoney(t.getMoney());
        th.setStage(t.getStage());
        th.setPossibility(t.getPossibility());
        int count1 = tranHistoryDao.save(th);
        if(count1 != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public PaginationVo<Map<String, Object>> getCharts() {
        int total = tranDao.getTotal();
        List<Map<String, Object>> dataList = tranDao.getCharts();
        PaginationVo<Map<String, Object>> vo = new PaginationVo<>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        return vo;
    }
}
