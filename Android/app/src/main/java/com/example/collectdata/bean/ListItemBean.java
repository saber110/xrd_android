package com.example.collectdata.bean;

import com.example.collectdata.tools.ConstTools;

import java.util.ArrayList;
import java.util.List;

public class ListItemBean extends CommonItemBean {

    private int length ;

    private List<CommonItemBean> innerItemList = new ArrayList<>();

    public ListItemBean(String title,int length) {
        super(title, null, ConstTools.MESSAGE_BEANTYPE_TITLELINE);
        this.length = length;
    }

    public List<CommonItemBean> getInnerItemList() {
        return innerItemList;
    }

    public void setInnerItemList(List<CommonItemBean> innerItemList) {
        this.innerItemList = innerItemList;
    }

    public void addInnerItem(CommonItemBean commonItemBean){
        innerItemList.add(commonItemBean);
    }

    public CommonItemBean getInnerItem(int index){
        return innerItemList.get(index);
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        super.toString();
        return "ListItemBean{" +
                "length=" + length +
                '}';
    }
}
