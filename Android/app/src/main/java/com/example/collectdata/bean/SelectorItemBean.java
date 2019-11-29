package com.example.collectdata.bean;

import com.example.collectdata.tools.ConstTools;

import java.util.ArrayList;
import java.util.List;

public class SelectorItemBean extends CommonItemBean {

    private List<String> data ;
    private String currentSelect;
    private List<String> currentSelects;
    private boolean isSingle;



    public SelectorItemBean(String title, List<String> data,boolean isSingle) {
        super(title, "",ConstTools.MESSAGE_BEANTYPE_SELECTOR);
        this.data = data;
        this.isSingle = isSingle;
        if (!isSingle)
            currentSelects = new ArrayList<>();
    }

    public SelectorItemBean(String title, List<String> data,String currentSelect) {
        this(title,data,true);
        this.currentSelect = currentSelect;
    }

    public SelectorItemBean(String title,List<String> data,List<String> currentSelects) {
        this(title,data,false);
        this.currentSelects = currentSelects;
    }

    public SelectorItemBean(String title,String[] dataArr,boolean isSingle) {
        super(title,"",ConstTools.MESSAGE_BEANTYPE_SELECTOR);
        //初始化data
        data = new ArrayList<>();
//        data.add("--请选择" + title + "--");
        for (String dot :
                dataArr) {
            data.add(dot);
        }
        if (!isSingle)
            currentSelects = new ArrayList<>();
    }

    public String[] getDataArr() {
        String[] dataArr = new String[data.size()];
        for (int i = 0 ; i < data.size() ; i ++ ) {
            dataArr[i] = data.get(i);
        }
        return dataArr;
    }


    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public String getCurrentSelect() {
        return currentSelect;
    }

    public void setCurrentSelect(String currentSelect) {
        this.currentSelect = currentSelect;
    }

    public List<String> getCurrentSelects() {
        return currentSelects;
    }

    public void setCurrentSelects(List<String> currentSelects) {
        this.currentSelects = currentSelects;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    @Override
    public String toString() {
        super.toString();
        return "SelectorItemBean{" +
                "data=" + data +
                ", currentSelect='" + currentSelect + '\'' +
                ", currentSelects=" + currentSelects +
                ", isSingle=" + isSingle +
                '}';
    }
}


