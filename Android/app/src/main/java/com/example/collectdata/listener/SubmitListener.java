package com.example.collectdata.listener;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.collectdata.DataActivity;
import com.example.collectdata.bean.CommonItemBean;
import com.example.collectdata.bean.MessageListBean;
import com.example.collectdata.bean.ViewItemBean;
import com.example.collectdata.exception.PageInitException;
import com.example.collectdata.tools.CacheTools;
import com.example.collectdata.tools.ConstTools;
import com.example.collectdata.tools.IntentTools;
import com.example.login.login;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by qiumengsong on 2019/11/9.
 */

public class SubmitListener implements View.OnClickListener {

    private Activity activity;
    private Map<String,ViewItemBean> viewItemBeanMap;
    private static final String ROOT_URL = "http://kms.yinaoxiong.cn:8888/api/v1/data/";
    public final List<CommonItemBean> list = MessageListBean.list;
    public SubmitListener(Activity activity,Map<String,ViewItemBean> viewItemBeanMap) {
        this.activity = activity;
        this.viewItemBeanMap = viewItemBeanMap;
    }

    @Override
    public void onClick(View v) {
        List<CommonItemBean> beans;
        HashMap<String,Object> map = new HashMap<>();
        try {
            beans = MessageListBean.getInstance(CacheTools.pageType).getList();
        } catch (Exception e) {
            Toast.makeText(activity,e.getMessage(),Toast.LENGTH_LONG).show();
            return ;
        }

        for (int i = 0 ; i < beans.size() ; i ++) {
            //先获取bean
            CommonItemBean commonItemBean = beans.get(i);
            String title = commonItemBean.getTitle();
            boolean required = commonItemBean.isRequire();
            String requiredType = commonItemBean.getRequireType();

            Log.i("Submit",title+" "+commonItemBean.getKey());

            ViewItemBean viewItemBean = viewItemBeanMap.get(title);
            //判断必填项是否完成
            if (required && viewItemBean == null) {
                Toast.makeText(activity, "请输入必填信息"  , Toast.LENGTH_SHORT).show();
                return;//如果未获取到，则不更新
            }
            if (viewItemBean.getType() == ConstTools.MESSAGE_BEANTYPE_COMMON) {
                //获取输入框的内容，edittext

                String content = viewItemBean.getEditText().getText().toString().trim();
                //number类型
                if (requiredType.equals("number") && !content.equals("")){
                    map.put(commonItemBean.getKey(), Integer.parseInt(content));
                }else {
                    commonItemBean.setContent(content);
                    map.put(commonItemBean.getKey(), content);
                }
            } else if (viewItemBean.getType() == ConstTools.MESSAGE_BEANTYPE_SELECTOR) {
                //不需要做任何操作
//                int focusedButtonId = viewItemBean.getButtonToggleGroup().getCheckedButtonId();
//                Button chosedButton = viewItemBean.getButtonToggleGroup().findViewById(focusedButtonId);
//                String content = chosedButton.getText().toString();
//                SelectorItemBean selectorItemBean = (SelectorItemBean)beans.get(i);
//                selectorItemBean.setCurrentSelect(content);
//                map.put(commonItemBean.getKey(),content);
                List<Integer> ids = viewItemBean.getButtonToggleGroup().getCheckedButtonIds();
                ArrayList<String> contents = new ArrayList<>();
                if (contents == null) {
                    Toast.makeText(activity, "输入必填项", Toast.LENGTH_SHORT).show();
                    return;
                }
                for(int id:ids){
                    Button chosed = viewItemBean.getButtonToggleGroup().findViewById(id);

                    contents.add(chosed.getText().toString());
                }
                 String content = "";
                for (String s : contents){
                    content += s;
                }
                map.put(commonItemBean.getKey(),content);
            }
            //if(commonItemBean.isChange()) map.put(commonItemBean.getKey(),"1");
        }

        map.put("token",login.token);
        map.put("collectTime",System.currentTimeMillis());
        Log.i("map",map.toString());
        System.out.println(map);
        try {
            if(CacheTools.pageType==1) {
                map.put("id",1);
                post(ROOT_URL + "garden_base_info", map);
            }
            if(CacheTools.pageType==2) {
                map.put("gardenId",1);
                post(ROOT_URL + "building_base_info", map);
            }
            if(CacheTools.pageType==3) {
                map.put("id",1);
                post(ROOT_URL + "garden_import_info", map);
            }
            if(CacheTools.pageType==4) {
                map.put("id",2);
                post(ROOT_URL + "building_import_info", map);
            }
        } catch (
    IOException e) {
            e.printStackTrace();
        }
        //Toast.makeText(activity, ConstTools.MESSAGE_SUBMIT_SUCCESS,Toast.LENGTH_LONG).show();
        IntentTools.activitySwich(activity, DataActivity.class,true);

    }

    public void post(final String url, final HashMap<String,Object> map) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("http2", login.token);
                RequestBody body = RequestBody.create(JSON.toJSONString(map), login.JSONDATA);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                new OkHttpClient().newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.i("http", "onFailure: " + e.toString() );
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try {
                            String result = response.body().string();
                            Log.i("http2", "onResponse: " + result);
                            JSONObject jsonObject = new JSONObject(result);
                            int code = jsonObject.getInt("code");
                            String msg = jsonObject.getString("message");
                            Log.i("test", msg);
                            //Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();
                        }catch (Exception e) {
                            Log.i("error","未知错误");
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
}