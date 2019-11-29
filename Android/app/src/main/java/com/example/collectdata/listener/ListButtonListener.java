package com.example.collectdata.listener;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.collectdata.tools.HttpTools;

public class ListButtonListener implements View.OnClickListener {

    private final int position;

    public ListButtonListener(int position) {
        this.position = position;
    }

    @Override
    public void onClick(View view) {
        //获取按钮的内容
        Button clickedButton = (Button)view;
        String content = clickedButton.getText().toString();
        //TODO 修改内存数据,判断是否为innerItem
//        MessageListBean.setCurrentSelect(position,content);

        Log.i("ListButtonListener","修改第"+position+"列为:" + content);

//        testHttpTools();
    }

    //测试请求数据
    public void testHttpTools(){
        Log.i("ListButtonListener","测试HttpTools");
        HttpTools httpTools = new HttpTools();
        String response = httpTools.request("",null);
        Log.i("ListButtonListener","测试结果为:" + response);

    }

}
