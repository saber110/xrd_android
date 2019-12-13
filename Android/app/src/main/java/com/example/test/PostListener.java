package com.example.test;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.collectdata.NearByActivity;
import com.example.collectdata.bean.CommonItemBean;
import com.example.login.login;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PostListener implements View.OnClickListener {
    private Map<String, String> map;
    private List<CommonItemBean> list;
    private RequestListener listener;
    private String mode;
    private Context context;
    private Map<String, Object> map2;

    public PostListener(Context context, Map<String, String> map, List<CommonItemBean> list, String mode, Map<String, Object> map2, RequestListener listener) {
        this.map = map;
        this.list = list;
        this.listener = listener;
        this.mode = mode;
        this.context = context;
        this.map2 = map2;
    }

    @Override
    public void onClick(View v) {
        HashMap<String, Object> submit_map = new HashMap<>();
        for (CommonItemBean bean : list) {
            boolean isRequire = bean.isRequire();
            String require_type = bean.getRequireType();
            String title = bean.getTitle();
            String key = bean.getKey();
            if (isRequire) {
                String content = map.get(title);
                if (content == null) {
                    Toast.makeText(context, "请输入必填项:"+title, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (require_type.equals("text"))
                    submit_map.put(key, content);
                else if (require_type.equals("radio"))
                    submit_map.put(key, content);
                else if (require_type.equals("multiple"))
                    submit_map.put(key, content);
                else if (require_type.equals("number")) {
                    int num = 0;
                    try {
                        num = Integer.parseInt(content);
                    } catch (Exception e) {
                        Toast.makeText(context, title + "项必须为数字", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    submit_map.put(key, num);
                }
            }else {
                String content = map.get(title);
                    if (content == null){
                        submit_map.put(key, "");
                    }else {
                        if (require_type.equals("text"))
                            submit_map.put(key, content);
                        else if (require_type.equals("number")) {
                            int num = 0;
                            try {
                                num = Integer.parseInt(content);
                            }catch (Exception e){
                                Toast.makeText(context,title + "项必须为数字",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            submit_map.put(key, num);
                        }
                        else{
                            submit_map.put(key, content);
                        }
                    }
                }
        }


        for (
                String s : NearByActivity.map.keySet()) {
            submit_map.put(s, String.valueOf(NearByActivity.map.get(s)));
        }

        for (
                String s : submit_map.keySet()) {
            Log.i("submitmap", s + ":" + submit_map.get(s));
        }

        submit_map.put("token", login.token);
        submit_map.put("collectTime", System.currentTimeMillis());
        submit_map.put("id", 2);

        if (mode.equals("base")) {
            String s = "http://kms.yinaoxiong.cn:8888/api/v1/data/" + "garden_base_info";
            new RequestTools(s, submit_map, listener).run();
        } else if (mode.equals("extra")) {
            String s = "http://kms.yinaoxiong.cn:8888/api/v1/data/" + "garden_import_info";
            new RequestTools(s, submit_map, listener).run();
        }
    }
}
