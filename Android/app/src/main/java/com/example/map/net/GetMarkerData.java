package com.example.map.net;

import android.util.Log;

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
    private Integer gardenId;
    private Integer mapId;

    public GetMarkerData(Integer gardenId, Integer mapId) {
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
            String res = request.body();
            Log.d(">>>>>>", "mark的查询数据: "+res);
            return JSONObject.parseObject(res, MapMarkerDataDao.class);
        } catch (Exception e) {
            return null;
        }
    }
}
