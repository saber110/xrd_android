package com.example.collectdata_01.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.collectdata_01.CollectActivity;
import com.example.collectdata_01.MainActivity;
import com.example.collectdata_01.R;
import com.example.collectdata_01.ResultActivity;

/**
 * 填充bottom的数据并且添加监听器
 */
public class BottomUtil {

    private BottomNavigationBar bottomNavigationBar;
    private Context context;
    /**
     * 目前activity的位置
     */
    private int position;

    public BottomUtil(BottomNavigationBar bottomNavigationBar, Context context, int position) {
        this.bottomNavigationBar = bottomNavigationBar;
        this.context = context;
        this.position = position;
    }


    /**
     * 设置样式并添加监听器
     */
    public void setBottomBarStytle() {
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_home_white_24dp, "首页"))
                .addItem(new BottomNavigationItem(R.drawable.ic_book_white_24dp, "照片采集"))
                .addItem(new BottomNavigationItem(R.drawable.ic_music_note_white_24dp, "上传照片"))
                .addItem(new BottomNavigationItem(R.drawable.ic_tv_white_24dp, "数据采集"))
                .initialise();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
                                                       @Override
                                                       public void onTabSelected(int po) {
                                                           /**
                                                            * 假如点击的该页面的按钮
                                                            */
                                                           if (po == position){
                                                               return;
                                                           }
                                                           switch (po){
                                                               case 0:
                                                                   context.startActivity(new Intent(context, MainActivity.class));

                                                                   break;
                                                               case 1:
                                                                   Intent intent =new Intent(context,MainActivity.class);
                                                                   intent.putExtra("flag", "true");
                                                                   context.startActivity(intent);
                                                                   break;
                                                               case 2:
                                                                   context.startActivity(new Intent(context, ResultActivity.class));
                                                                   break;
                                                               case 3:

                                                                   context.startActivity(new Intent(context, CollectActivity.class));
                                                                   break;
                                                               default:break;
                                                           }
                                                       }

                                                       @Override
                                                       public void onTabUnselected(int position) {
                                                       }

                                                       @Override
                                                       public void onTabReselected(int position) {
                                                       }
                                                   }
        );
    }
}
