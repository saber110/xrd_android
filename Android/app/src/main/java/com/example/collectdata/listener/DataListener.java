package com.example.collectdata.listener;


import android.app.Activity;
import android.view.View;

import com.example.collectdata.MessageActivity;
import com.example.collectdata.tools.CacheTools;
import com.example.collectdata.tools.IntentTools;

/**
 * 数据收集页面的监听器
 */
public class DataListener implements View.OnClickListener {

    private Activity activity;
    private final int type;

    public DataListener(Activity activity,int listenerType) {
        this.type = listenerType;
        this.activity = activity;
    }


    public void onClick(View view) {
        CacheTools.pageType = type;
        //转跳到对应的信息收集页面
        IntentTools.activitySwich(activity, MessageActivity.class,true);
    }
}
