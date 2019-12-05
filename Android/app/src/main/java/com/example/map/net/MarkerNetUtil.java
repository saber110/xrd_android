package com.example.map.net;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.interfaceNet.v1;
import com.example.login.login;
import com.example.map.dao.MapData;
import com.example.map.dao.MapMarkerDataDao;
import com.example.map.dao.StanderDao;
import com.example.map.dao.UploadMarkerReturnDao;
import com.example.net.ProcessInterface;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class MarkerNetUtil  {

    /**
     * 删除数据
     */
    public static class DeletMarkerUtil implements  ProcessInterface{

        private Integer id;

        public DeletMarkerUtil(Integer id) {
            this.id = id;
        }

        @Override
        public Object call() {
            Map map = new HashMap(2);
            map.put("token", login.token);
            map.put("id", id);
            try {
                HttpRequest request = new HttpRequest(v1.deleteMapDataApi, "POST")
                        .header("Content-Type", "application/json")
                        .send(JSON.toJSONString(map));
                String result = request.body();
                Log.d(">>>>", "call: "+result);
                return JSONObject.parseObject(result, StanderDao.class);
            } catch (Exception e) {
                return null;
            }
        }
    }


    /**
     * 获得数据
     */
    public static class GetMarkerData implements ProcessInterface {
        private Integer gardenId;
        private int mapId;

        public GetMarkerData(Integer gardenId, int mapId) {
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


    /**
     * 添加或则修改数据
     */
    public static class AddMarker implements ProcessInterface {

        private MapData mapData = new MapData();

        public AddMarker(double lat, double lg, String name, Integer gardenId, int mapId, int kindId) {
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
                return JSONObject.parseObject(result, UploadMarkerReturnDao.class);
            } catch (Exception e) {
                return null;
            }
        }
    }


}
