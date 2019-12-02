package com.example.map.net;

import com.alibaba.fastjson.JSONObject;
import com.example.login.login;
import com.example.map.dao.MapMarkerDataDao;
import com.example.net.ProcessInterface;
import com.github.kevinsawicki.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class GetMarkerData implements ProcessInterface {
    private String gardenId;
    private int mapId;

    public GetMarkerData(String gardenId, int mapId) {
        this.gardenId = gardenId;
        this.mapId = mapId;
    }

    @Override
    public Object call() {
        Map map = new HashMap(3);
        map.put("token", login.token);
        map.put("gardenId", gardenId);
        map.put("mapId", mapId);

        try {
            HttpRequest request = new HttpRequest("http://rap2api.taobao.org/app/mock/234350/api/v1/get_data/map", "POST").form(map);
            return JSONObject.parseObject(request.body(), MapMarkerDataDao.class);
        } catch (Exception e) {
            return null;
        }
    }
}
