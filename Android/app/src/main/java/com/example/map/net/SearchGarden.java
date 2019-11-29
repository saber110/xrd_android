package com.example.map.net;

import com.alibaba.fastjson.JSONObject;
import com.example.map.dao.SearchGardenResultDao;
import com.example.net.ProcessInterface;
import com.github.kevinsawicki.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class SearchGarden implements ProcessInterface {
    private String token;

    public SearchGarden(String token) {
        this.token = token;
    }

    @Override
    public Object call() {

        Map map = new HashMap(1);
        map.put("token", token);
        try {
            HttpRequest request = new HttpRequest("http://rap2api.taobao.org/app/mock/234350/api/v1/data/building", "POST").form(map);
            SearchGardenResultDao searchGardenResultDao = JSONObject.parseObject(request.body(), SearchGardenResultDao.class);
            return searchGardenResultDao;
        } catch (Exception e) {
            return null;
        }
    }
}
