package com.example.map.net;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.navi.INavi;
import com.example.interfaceNet.v1;
import com.example.login.login;
import com.example.map.dao.MapData;
import com.example.map.dao.StanderDao;
import com.example.net.ProcessInterface;
import com.github.kevinsawicki.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class SendMapMsg implements ProcessInterface {

    private MapData mapData = new MapData();

    /**
     * {
     *   "longitude": 1,
     *   "latitude": 1,
     *   "kindId": 1,
     *   "name": "",
     *   "gardenId": 1,
     *   "token": "",
     *   "mapId": 0,
     *   "id": "地图数据id"
     * }
     * @param lat
     * @param lg
     * @param name
     * @param gardenId
     * @param mapId
     * @param kindId
     */
    public SendMapMsg(double lat, double lg, String name, Integer gardenId, int mapId, int kindId) {
        mapData.setLatitude(lat);
        mapData.setLongitude(lg);
        mapData.setKindId(kindId);
        mapData.setName(name);
        mapData.setGardenId(gardenId);
        mapData.setToken(login.token);
        mapData.setMapId(mapId);
    }

    @Override
    public Object call() {
        Map map = new HashMap(7);
        map.put("longitude", mapData.getLongitude());
        map.put("latitude", mapData.getLatitude());
        map.put("kindId", mapData.getKindId());
        map.put("name", mapData.getName());
        map.put("gardenId", mapData.getGardenId());
        map.put("token", mapData.getToken());
        map.put("mapId", mapData.getMapId());

        try {
            HttpRequest request = new HttpRequest(v1.addMapDataAPI, "POST")
                    .header("Content-Type", "application/json")
                    .send(JSON.toJSONString(map));
            String result = request.body();
            Log.d(">>>>", "添加: "+result);
            return JSONObject.parseObject(result, StanderDao.class);
        } catch (Exception e) {
            return null;
        }
    }
}
