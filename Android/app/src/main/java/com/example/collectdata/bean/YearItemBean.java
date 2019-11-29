package com.example.collectdata.bean;

import com.example.collectdata.tools.ConstTools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class YearItemBean extends SelectorItemBean {

    public final int type = ConstTools.MESSAGE_BEANTYPE_SELECTOR;

    //起始年份
    private static final int START_YEAR = 1970;

    public YearItemBean(String title) {
        super(title, new ArrayList<String>(),true);
        //初始换年份列表
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        List<String> yearList = new ArrayList<>();
//        yearList.add("请输入年份...");
        for (int i = currentYear ; i >= START_YEAR ; i --) {
            yearList.add(i + "");
        }
        super.setData(yearList);
        super.setCurrentSelect(yearList.get(0));
    }

}
