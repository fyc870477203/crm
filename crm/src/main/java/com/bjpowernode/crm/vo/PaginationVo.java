package com.bjpowernode.crm.vo;

import java.util.List;

/**
 * 作者:fyc
 * 2019/8/31
 */
public class PaginationVo<T>{
    private List<T> dataList;
    private int total;

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
