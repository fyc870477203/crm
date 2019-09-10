package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVo;
import com.bjpowernode.crm.workbench.dao.*;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者:fyc
 * 2019/8/30
 */
public class ClueServiceImpl implements ClueService {
    //线索相关表
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);

    //客户相关表
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    //联系人相关表
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    //交易相关表
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    @Override
    public boolean save(Clue c) {
        boolean flag =true;
        int count = clueDao.save(c);
        if(count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Clue detail(String id) {
        Clue c = clueDao.detail(id);
        return c;
    }

    @Override
    public boolean unbund(String id) {
        boolean flag = true;
        int count = clueActivityRelationDao.unbund(id);
        if(count != 1){
            flag =false;
        }
        return flag;
    }

    @Override
    public boolean bund(String clueId, String[] activityIds) {
        boolean flag = true;
        //遍历市场活动Id数组
        //取得每一个activityId
        for(String activityId:activityIds){
            //由遍历出来的每一个activityId和clueId做关联操作
            //执行线索和市场活动关联关系表的添加操作
            ClueActivityRelation car = new ClueActivityRelation();
            car.setId(UUIDUtil.getUUID());
            car.setClueId(clueId);
            car.setActivityId(activityId);
            int count = clueActivityRelationDao.save(car);
            if(count != 1){
                flag=false;
            }
        }
        return flag;
    }

    @Override
    public boolean convert(String clueId, Tran t, String createBy) {
        boolean flag = true;
        String createTime = DateTimeUtil.getSysTime();
        /*
            线索的转换，指的是讲过线索(潜在用户)转变为真是的客户和联系人
            主线:
                insert 客户
                insert 联系人
                delete 线索
         */
        /*
            1.通过clueId查询线索的单条信息，这条信息查询的目的是为了一会提取出公司信息生成客户
            提取出人的信息生成联系人
         */
        Clue c = clueDao.getById(clueId);
        /*
            从线索对象c中，取出公司名称company，通过该公司名称到客户表中进行查询，看看有没有这个公司
            如果没有这个公司，则根据该公司名称company，新建一个客户
            值得注意的是:
            1).查询条件公司名称，，一定要使用=号进行精确匹配绝对不能使用like进行模糊匹配
            where name = ？
            2).查询的结果，应该返回
            Customer cus = select * from tbl_customer where
            cus == null,说明没有这条记录，需要新建一个客户
            cus != null,说明查到了这个客户，不需要新建
         */
        String company = c.getCompany();
        Customer cus=customerDao.getCustomerByName(company);
        //如果cus为null，说明没有这个客户需要新建一个客户
        if(cus == null){
            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setName(company);
            cus.setWebsite(c.getWebsite());
            cus.setPhone(c.getPhone());
            cus.setOwner(c.getOwner());
            cus.setNextContactTime(c.getNextContactTime());
            cus.setDescription(c.getDescription());
            cus.setCreateBy(createBy);
            cus.setCreateTime(createTime);
            cus.setContactSummary(c.getContactSummary());
            cus.setAddress(c.getAddress());

            //添加客户
            int count1 = customerDao.save(cus);
            if(count1 != 1){
                flag = false;
            }
        }
        /*
            以上第二步执行完毕后如果接下来的步骤需要使用客户的Id,我们就是用cus.getId()
         */
        /*
            3.将c中的与人相关的信息，提取出来生产联系人
            注意：生成联系人不需要过多的判断
            一个客户有可能有多个联系人，联系人记录是可以重复的
         */
        //添加联系人
        Contacts con =new Contacts();
        con.setId(UUIDUtil.getUUID());
        con.setSource(c.getSource());
        con.setOwner(c.getOwner());
        con.setNextContactTime(c.getNextContactTime());
        con.setMphone(c.getMphone());
        con.setJob(c.getJob());
        con.setFullname(c.getFullname());
        con.setEmail(c.getEmail());
        con.setDescription(c.getDescription());
        con.setCustomerId(cus.getId());
        con.setCreateBy(createBy);
        con.setCreateTime(createTime);
        con.setContactSummary(c.getContactSummary());
        con.setAppellation(c.getAppellation());
        c.setAddress(c.getAddress());
        int count2 = contactsDao.save(con);
        if(count2 != 1){
            flag = false;
        }
        //以上第三步执行完毕后，如果接下来的步骤需要使用到联系人的id就直接调用con.getId()
        /*
            4.将需要转换的线索关联的备注信息，备份到客户的备注表当中去
         */
        //查询与该线索关联的备注信息列表
        List<ClueRemark> clueRemarkList = clueRemarkDao.getListById(clueId);
        //遍历每一条线索备注信息
        for(ClueRemark clueRemark:clueRemarkList){
            //取得每一条需要备份的备注信息
            String noteContent = clueRemark.getNoteContent();

            //创建客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setNoteContent(noteContent);
            customerRemark.setEditFlag("0");
            customerRemark.setCustomerId(cus.getId());
            customerRemark.setCreateTime(createTime);
            customerRemark.setCreateBy(createBy);
            int count3 = customerRemarkDao.save(customerRemark);
            if(count3 != 1){
                flag=false;
            }
            //创建联系人备注
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setNoteContent(noteContent);
            contactsRemark.setEditFlag("0");
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setContactsId(con.getId());
            int count4=contactsRemarkDao.save(contactsRemark);
            if(count4 != 1){
                flag = false;
            }
        }
        /*
            5.将线索与市场活动关联关系表中的信息备份到联系人和市场活动关联关系表当中
         */
        //查询线索与市场活动的关联关系列表
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getListById(clueId);
        for(ClueActivityRelation clueActivityRelation:clueActivityRelationList){
            //取出需要备份的市场活动id，让市场活动id和联系人做新的关联
            String activityId = clueActivityRelation.getActivityId();
            //添加联系人和市场活动的关联关系
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(con.getId());
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            int count5 =contactsActivityRelationDao.save(contactsActivityRelation);
            if(count5 != 1){
                flag = false;
            }
        }
        /*
            6.如果有创建交易的需求，需要添加一条交易
            根据t是否为null，来判断是否需要创建交易
            */
        if(t != null){
            /*  进入到if体，说明需要创建交易

                t对象中已经封装过了一些重要属性的信息
                    id,money,name,expectedDate,stage,activityId,createBy,createBy,createTime
                至于其他的心，我们可以根据c对象来进行转换
            */
            //添加交易
            t.setSource(c.getSource());
            t.setOwner(c.getOwner());
            t.setNextContactTime(c.getNextContactTime());
            t.setDescription(c.getDescription());
            t.setCustomerId(cus.getId());
            t.setContactSummary(c.getContactSummary());
            t.setContactsId(con.getId());

            int count6 = tranDao.save(t);
            if(count6 != 1){
                flag = false;
            }
            /*
            7.如果已经创建了交易，我们应该伴随着这条交易，生成一条交易历史
                注意：创建交易历史，必须紧跟在创建交易的后面不能出if体
            */
            TranHistory th = new TranHistory();
            th.setId(UUIDUtil.getUUID());
            th.setCreateBy(createBy);
            th.setCreateTime(createTime);
            th.setExpectedDate(t.getExpectedDate());
            th.setTranId(t.getId());
            th.setMoney(t.getMoney());
            th.setStage(t.getStage());
            int count7 = tranHistoryDao.save(th);
            if(count7 != 1){
                flag = false;
            }
        }
        /*
            8.将线索关联的备注信息删除掉
         */
        for(ClueRemark clueRemark:clueRemarkList){
            int count8 = clueRemarkDao.delete(clueRemark);
            if(count8 != 1){
                flag = false;
            }
        }
        /*
            9.将线索与市场活动的关联关系列表数据删除掉
         */
        for(ClueActivityRelation clueActivityRelation:clueActivityRelationList){
            int count9 = clueActivityRelationDao.delete(clueActivityRelation);
            if(count9 != 1){
                flag = false;
            }
        }
        /*
            10.删除线索
         */
        int count10 = clueDao.delete(clueId);
        if(count10 != 1){
            flag = false;
        }
        return flag;
    }


}
