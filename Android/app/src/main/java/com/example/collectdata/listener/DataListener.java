package com.example.collectdata.listener;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.example.collectdata.tools.CacheTools;
import com.example.collectdata.tools.ConstTools;
import com.example.collectdata_01.R;
import com.example.dialog.CreatDialog;
import com.example.interfaceNet.v1;

/**
 * 数据收集页面的监听器
 */
public class DataListener implements View.OnClickListener {

    private Activity activity;
    private final int type;
    private Context context;
    private Dialog buildingNumDialog;
    private View BuildNumView;

    public DataListener(Activity activity,int listenerType, Context context) {
        this.type = listenerType;
        this.activity = activity;
        this.context = context;
    }

    public void onClick(View view) {
        CacheTools.pageType = type;
        CacheTools.listViewMessage.clear();
        // 获取工作小区id
        int gardenId = 1;
        int buildingId = 1;
        // loading
        Toast.makeText(activity,"数据加载中，请稍等...", Toast.LENGTH_SHORT ).show();
        // 请求后台数据并切换界面
        v1 interfaceForGetData = new v1(this.context);
        switch (type) {
            case ConstTools.LOUZHUANGDIAOCHA:
//                interfaceForGetData.getBuildingBaseInfoAPI(buildingId);
                break;
            case ConstTools.XIAOQUXINXI: interfaceForGetData.getGardenImportInfoAPI(gardenId);break;
            case ConstTools.LOUZHUANGXINXI: interfaceForGetData.getBuildingImportInfoAPI(buildingId);break;
            case ConstTools.XIAOQUGAIKUANG: interfaceForGetData.getGardenBaseInfoAPI(gardenId);
        }
    }
}
