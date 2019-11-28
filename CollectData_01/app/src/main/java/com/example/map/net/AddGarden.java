package com.example.map.net;

import com.alibaba.fastjson.JSONObject;
import com.example.map.dao.AddGradenDao;
import com.example.map.dao.AddGradenResult;
import com.example.net.ProcessInterface;
import com.github.kevinsawicki.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class AddGarden implements ProcessInterface {

    private AddGradenDao addGardenDao = new AddGradenDao();

    public AddGarden(String name) {
        addGardenDao.setToken("12345678");
        addGardenDao.setDistrictId(1);
        addGardenDao.setStreetId(1);
        addGardenDao.setCommunityId(1);
        addGardenDao.setGardenName(name);
    }

    @Override
    public Object call() {
        Map map = new HashMap(5);
        map.put("token", addGardenDao.getToken());
        map.put("districtId", addGardenDao.getDistrictId());
        map.put("streetId", addGardenDao.getStreetId());
        map.put("communityId", addGardenDao.getCommunityId());
        map.put("gardenName", addGardenDao.getGardenName());

        try {
            HttpRequest request = new HttpRequest("http://rap2api.taobao.org/app/mock/234350/api/v1/data/garden", "POST").form(map);
            AddGradenResult addGradenResult = JSONObject.parseObject(request.body(), AddGradenResult.class);
            return addGradenResult;
        } catch (Exception e) {
            return null;
        }
    }
}
