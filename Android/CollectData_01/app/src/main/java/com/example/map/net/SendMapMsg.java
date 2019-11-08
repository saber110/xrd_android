package com.example.map.net;

import com.example.map.dao.MapData;
import com.example.net.ProcessInterface;
import com.github.kevinsawicki.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class SendMapMsg implements ProcessInterface {

    private MapData mapData = new MapData();

    public SendMapMsg(double lat, double lg, String name, int gardenId, int mapId) {
        mapData.setLatitude(lat);
        mapData.setLongitude(lg);
        mapData.setKindId(1);
        mapData.setName(name);
        mapData.setGardenId(gardenId);
        mapData.setToken("12345678");
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
            HttpRequest request = new HttpRequest("http://rap2api.taobao.org/app/mock/234350/api/v1/data/map", "POST").form(map);
            return request.body();
        } catch (Exception e) {
            return null;
        }
    }
}
