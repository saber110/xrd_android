package com.example.collectdata.listener;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.collectdata.DataActivity;
import com.example.collectdata.bean.CommonItemBean;
import com.example.collectdata.bean.MessageListBean;
import com.example.collectdata.bean.ViewItemBean;
import com.example.collectdata.exception.PageInitException;
import com.example.collectdata.tools.CacheTools;
import com.example.collectdata.tools.ConstTools;
import com.example.collectdata.tools.IntentTools;

import java.util.List;
import java.util.Map;


/**
 * Created by qiumengsong on 2019/11/9.
 */

public class SubmitListener implements View.OnClickListener {

    private Activity activity;
    private Map<String,ViewItemBean> viewItemBeanMap;

    public SubmitListener(Activity activity,Map<String,ViewItemBean> viewItemBeanMap) {
        this.activity = activity;
        this.viewItemBeanMap = viewItemBeanMap;
    }

    @Override
    public void onClick(View v) {
        List<CommonItemBean> beans;
        try {
            beans = MessageListBean.getInstance(CacheTools.pageType).getList();
        } catch (Exception e) {
            Toast.makeText(activity,e.getMessage(),Toast.LENGTH_LONG).show();
            return ;
        }
        Log.i("beansLength",beans.size() + "");
        Log.i("viewLength",viewItemBeanMap.size() + "");
        for (int i = 0 ; i < beans.size() ; i ++) {
            //先获取bean
            CommonItemBean commonItemBean = beans.get(i);
            String title = commonItemBean.getTitle();
            Log.i("Submit",title);
//            Log.i("Submit",beans.toString());
            //根据标题获取组建
            ViewItemBean viewItemBean = viewItemBeanMap.get(title);
            if (viewItemBean == null)
                continue;//如果未获取到，则不更新
            if (viewItemBean.getType() == ConstTools.MESSAGE_BEANTYPE_COMMON) {
                //获取输入框的内容
                String content = viewItemBean.getEditText().getText().toString().trim();
                //更新内容
                commonItemBean.setContent(content);
            } else if (viewItemBean.getType() == ConstTools.MESSAGE_BEANTYPE_SELECTOR) {
                //不需要做任何操作
//                int focusedButtonId = viewItemBean.getButtonToggleGroup().getCheckedButtonId();
//                Button chosedButton = viewItemBean.getButtonToggleGroup().findViewById(focusedButtonId);
//                String content = chosedButton.getText().toString();
//                SelectorItemBean selectorItemBean = (SelectorItemBean)beans.get(i);
//                selectorItemBean.setCurrentSelect(content);
            }
        }

        Toast.makeText(activity, ConstTools.MESSAGE_SUBMIT_SUCCESS,Toast.LENGTH_LONG).show();
        IntentTools.activitySwich(activity, DataActivity.class,true);
    }
}