package com.example.map.net;

import com.alibaba.fastjson.JSONObject;
import com.example.interfaceNet.v1;
import com.example.login.login;
import com.example.map.dao.AddGradenDao;
import com.example.map.dao.AddGradenResult;
import com.example.net.ProcessInterface;
import com.github.kevinsawicki.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class AddGarden implements ProcessInterface {

    private AddGradenDao addGardenDao = new AddGradenDao();

    public AddGarden(String name,Integer provinceId,Integer cityId,Integer districtId,Integer streetId,Integer communityId) {
        addGardenDao.setToken(login.token);
        addGardenDao.setProvinceId(provinceId);
        addGardenDao.setCityId(cityId);
        addGardenDao.setDistrictId(districtId);
        addGardenDao.setStreetId(streetId);
        addGardenDao.setCommunityId(communityId);
        addGardenDao.setGardenName(name);
    }

    @Override
    public Object call() {
        Map map = new HashMap(5);
        map.put("token", addGardenDao.getToken());
        map.put("provinceId", addGardenDao.getProvinceId());
        map.put("cityId", addGardenDao.getCityId());
        map.put("districtId", addGardenDao.getDistrictId());
        map.put("streetId", addGardenDao.getStreetId());
        map.put("communityId", addGardenDao.getCommunityId());
        map.put("gardenName", addGardenDao.getGardenName());

        try {
            HttpRequest request = new HttpRequest(v1.createGardenApi, "POST").form(map);
            AddGradenResult addGradenResult = JSONObject.parseObject(request.body(), AddGradenResult.class);
            return addGradenResult;
        } catch (Exception e) {
            return null;
        }
    }
}
