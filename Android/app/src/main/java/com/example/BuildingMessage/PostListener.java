package com.example.BuildingMessage;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.collectdata.BuweiActivity;
import com.example.collectdata.bean.CommonItemBean;
import com.example.collectdata.bean.ListItemBean;
import com.example.collectdata.bean.SelectorItemBean;
import com.example.interfaceNet.v1;
import com.example.login.login;
import com.example.test.RequestListener;
import com.example.test.RequestTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostListener implements View.OnClickListener {
    private Map<String, String> map, map2;
    private List<CommonItemBean> list;
    private RequestListener listener;
    private String mode;
    private Context context;
    private int id;
    private int gardenId;

    public PostListener(Context context, int gardenId, Map<String, String> map, List<CommonItemBean> list, String mode, int id, Map<String, String> map2, RequestListener listener) {
        this.map = map;
        this.list = list;
        this.listener = listener;
        this.mode = mode;
        this.context = context;
        this.id = id;
        this.gardenId = gardenId;
        this.map2 = map2;
    }

    @Override
    public void onClick(View v) {
        HashMap<String, Object> submit_map = new HashMap<>();
        for (CommonItemBean bean : list) {
            // 对listItemBean单独处理
            if (bean instanceof ListItemBean) {
                for (CommonItemBean commonItemBean : ((ListItemBean) bean).getInnerItemList()) {
                    String content;
                    if (commonItemBean instanceof SelectorItemBean)
                        content = map.get(commonItemBean.getTitle());
                    else
                        content = map.get(bean.getTitle() + commonItemBean.getTitle());
                    if (content == null || content.equals(""))
                        continue;
                    if (commonItemBean.getRequireType().equals("number")) {
                        double num = 0;
                        try {
                            num = Double.parseDouble(content);
                        } catch (Exception e) {
                            Toast.makeText(context, bean.getTitle() + commonItemBean.getTitle() + "项必须为数字", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        submit_map.put(commonItemBean.getKey(), num);
                    } else {
                        submit_map.put(commonItemBean.getKey(), content);
                    }
                }
            } else {
                String require_type = bean.getRequireType();
                String title = bean.getTitle();
                String key = bean.getKey();

                String content = map.get(title);
                if (content == null || content.equals(""))
                    continue;
                if (require_type.equals("number")) {
                    double num = 0.0;
                    try {
                        num = Double.parseDouble(content);
                    } catch (Exception e) {
                        Toast.makeText(context, title + "项必须为数字", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    submit_map.put(key, num);
                } else {
                    submit_map.put(key, content);
                }


            }
        }

        for (String s : submit_map.keySet()) {
            Log.i("submit_map", "submitMap : " + s + ":" + submit_map.get(s));
        }

        submit_map.put("token", login.token);
        submit_map.put("collectTime", System.currentTimeMillis());
        if (id < 0)
            ;
        else
            submit_map.put("id", id);
        submit_map.put("gardenId", gardenId);

        if (mode.equals("base")) {
            submit_map.put("locationDescription", BuildingActivity.buweiMap.get(id));
            System.out.println(id+" "+submit_map);
            String url = "http://gjb.hnxrd.com.cn:9998/api/v1/data/" + "building_base_info";
            new RequestTools(v1.updateBuildingBaseInfoApi, submit_map, listener).run();
        } else if (mode.equals("extra")) {
            String url = "http://gjb.hnxrd.com.cn:9998/api/v1/data/" + "building_import_info";
            new RequestTools(v1.updateBuildingImportInfoApi, submit_map, listener).run();
        }
    }

}

