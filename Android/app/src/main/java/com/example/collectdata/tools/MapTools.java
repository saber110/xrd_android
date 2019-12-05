package com.example.collectdata.tools;

import android.app.Activity;

import com.baidu.location.LocationClient;

/**
 * 地图工具
 */
public class MapTools {

    private Activity activity;

    public void locate(){
        LocationClient locationClient = new LocationClient(activity.getApplicationContext());

    }

}
