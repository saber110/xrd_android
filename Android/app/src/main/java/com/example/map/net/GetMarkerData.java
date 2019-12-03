package com.example.map.net;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.interfaceNet.v1;
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
            HttpRequest request = new HttpRequest(v1.getMapDataAPI, "POST")
                    .header("Content-Type", "application/json")
                    .send(JSON.toJSONString(map));
            return JSONObject.parseObject(request.body(), MapMarkerDataDao.class);
        } catch (Exception e) {
            return null;
        }
    }
}
