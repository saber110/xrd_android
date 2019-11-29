package com.example.collectdata.tools;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by qiumengsong on 2017/4/22.
 */

public class IntentTools {

    /**
     * 旧页面从右侧退出 新页面从左侧进入
     */
    public static final int FROM_LEFT_TO_RIGHT = 1;
    /**
     * 旧页面从左侧退出 新页面从右侧进入
     */
    public static final int FROM_RIGTH_TO_LEFT = 2;

    /**
     * 页面跳转
     * @param oldActivity 源activity
     * @param newActivity 目的activity
     * @param isOldActivityFinish 是否终结源activity
     */
    public static void activitySwich(Activity oldActivity,Class newActivity,boolean isOldActivityFinish){

        Intent intent = new Intent(oldActivity,newActivity);
        oldActivity.startActivity(intent);
        if (isOldActivityFinish){
            oldActivity.finish();
        }

    }

    /**
     * 动画过度的页面切换
     * @param oldActivity
     * @param newActivity
     * @param isOldActivityFinish
     * @param animationType
     */
    public static void activitySwitchWithAnimation(Activity oldActivity,Class newActivity,boolean isOldActivityFinish,int animationType){
        activitySwich(oldActivity,newActivity,isOldActivityFinish);
        if (animationType == FROM_LEFT_TO_RIGHT){
//            oldActivity.overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

}
