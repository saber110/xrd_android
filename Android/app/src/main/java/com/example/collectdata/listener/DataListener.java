package com.example.collectdata.listener;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.example.BuildingMessage.BuildingActivity;
import com.example.collectdata.tools.CacheTools;
import com.example.collectdata.tools.ConstTools;
import com.example.collectdata_01.MainActivity;
import com.example.collectdata_01.R;
import com.example.interfaceNet.v1;
import com.example.test.GardenMessage;

/**
 * 数据收集页面的监听器
 */
public class DataListener implements View.OnClickListener {

    private Activity activity;
    private final int type;
    private Context context;
    private Dialog buildingNumDialog;
    private View BuildNumView;

    String pattern ;
    String getBuildingBaseInfoAPI;
    String getBuildingImportInfoApi;

    public DataListener(Activity activity, int listenerType, Context context) {
        this.type = listenerType;
        this.activity = activity;
        this.context = context;
        pattern = context.getResources().getString(R.string.preFix);
        getBuildingBaseInfoAPI = String.format(pattern, context.getResources().getString(R.string.getDataTab), context.getResources().getString(R.string.getBuildBaseInfoAPI));
        getBuildingImportInfoApi = String.format(pattern, context.getResources().getString(R.string.getDataTab), context.getResources().getString(R.string.getBuildingImportInfoApi));
    }

    public void onClick(View view) {
        CacheTools.pageType = type;
        CacheTools.listViewMessage.clear();
        // 获取工作小区id
        int gardenId = MainActivity.getGardenId();
        // loading
        Toast.makeText(activity,"数据加载中，请稍等...", Toast.LENGTH_SHORT ).show();
        // 请求后台数据并切换界面
        v1 interfaceForGetData = new v1(this.context);
        switch (type) {
            case ConstTools.LOUZHUANGDIAOCHA:
                //此处已没有楼栋接口的listener
                Intent BuildingDiaoChaIntent = new Intent(context, BuildingActivity.class);
                BuildingDiaoChaIntent.putExtra("gardenId",gardenId);
                BuildingDiaoChaIntent.putExtra("mode","base");
                BuildingDiaoChaIntent.putExtra("url",v1.getBuildingBaseInfoAPI);
                context.startActivity(BuildingDiaoChaIntent);
                break;
//                interfaceForGetData.getBuildingBaseInfoAPI(buildingId);
            case ConstTools.LOUZHUANGXINXI:
                Intent BuildingXinxiIntent = new Intent(context, BuildingActivity.class);
                BuildingXinxiIntent.putExtra("gardenId",gardenId);
                BuildingXinxiIntent.putExtra("mode","extra");
//                BuildingXinxiIntent.putExtra("url",getBuildingImportInfoApi);
                BuildingXinxiIntent.putExtra("url",v1.getBuildingImportInfoApi);
                context.startActivity(BuildingXinxiIntent);
                break;
//                interfaceForGetData.getBuildingImportInfoAPI(buildingId);break;

            case ConstTools.XIAOQUXINXI:
//                interfaceForGetData.getGardenImportInfoAPI(gardenId);break;
                Intent GardenImportIntent = new Intent(context, GardenMessage.class);
                GardenImportIntent.putExtra("gardenId",gardenId);
                GardenImportIntent.putExtra("mode","extra");
                GardenImportIntent.putExtra("url",v1.getGardenImportInfoApi);
                context.startActivity(GardenImportIntent);
                break;

            case ConstTools.XIAOQUGAIKUANG:
//                interfaceForGetData.getGardenBaseInfoAPI(gardenId);
                Intent GardenBaseIntent = new Intent(context, GardenMessage.class);
                GardenBaseIntent.putExtra("gardenId",gardenId);
                GardenBaseIntent.putExtra("mode","base");
                GardenBaseIntent.putExtra("url",v1.getGardenBaseInfoAPI);
                context.startActivity(GardenBaseIntent);
                break;
        }

    }
}
