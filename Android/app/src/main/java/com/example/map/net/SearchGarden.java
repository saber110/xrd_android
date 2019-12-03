package com.example.map.net;

import android.util.Log;

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
        Log.i(TAG, "SearchGardentoken: " + v1.searchGardenApi + " | "+ searchGardenDao.getGardenName());
        map.put("token", searchGardenDao.getToken());
        map.put("gardenName", searchGardenDao.getGardenName());
        try {
            HttpRequest request = new HttpRequest(v1.searchGardenApi, "POST").form(map);
            Log.i(TAG, "SearchGardencall: " + request.body());
            SearchGardenResultDao searchGardenResultDao = JSONObject.parseObject(request.body(), SearchGardenResultDao.class);
            return searchGardenResultDao;
        } catch (Exception e) {
            return null;
        }
    }
}
