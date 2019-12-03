package com.example.map.net;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.interfaceNet.v1;
import com.example.login.login;
import com.example.map.dao.AddGradenDao;
import com.example.map.dao.SearchGardenResultDao;
import com.example.net.ProcessInterface;
import com.github.kevinsawicki.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

import static com.litesuits.orm.db.impl.CascadeSQLiteImpl.TAG;

public class SearchGarden implements ProcessInterface {

    private AddGradenDao searchGardenDao = new AddGradenDao();

    public SearchGarden(String name) {
        searchGardenDao.setToken(login.token);
        searchGardenDao.setGardenName(name);
    }
    @Override
    public Object call() {

        Map map = new HashMap(1);
        map.put("token", login.token);
        map.put("gardenName", searchGardenDao.getGardenName());
        String param = JSON.toJSONString(map);
        try {
            HttpRequest request = new HttpRequest(v1.searchGardenApi, "POST")
                    .header("Content-Type", "application/json")
                    .send(param);
            Log.i(TAG, "SearchGardencall: " + request.body());

            // 接口已通，需要调整SearchGardenResultDao
            SearchGardenResultDao searchGardenResultDao = JSONObject.parseObject(request.body(), SearchGardenResultDao.class);
            return searchGardenResultDao;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
