package com.example.map.net;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.login.login;
import com.example.map.dao.CityDao;
import com.example.map.dao.CommunityDao;
import com.example.map.dao.DistrictDao;
import com.example.map.dao.ProvinceDao;
import com.example.map.dao.StreetDao;
import com.example.interfaceNet.v1;
import com.github.kevinsawicki.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 用来获取位置的ID
 */
public class GetLoctionIdUtil {

    /**
     * 获得省的数据
     * @return
     */
    public static ProvinceDao getProvinceId(){
        Map map = new HashMap(1);
        map.put("token", login.token);
        try {
            HttpRequest request = new HttpRequest(v1.getProvinceApi, "POST")
                    .header("Content-Type", "application/json")
                    .send(JSON.toJSONString(map));
            String result = request.body();
            ProvinceDao provinceDao = JSONObject.parseObject(result, ProvinceDao.class);
            Log.d(">>>>>", "省的数据: "+provinceDao);
            return provinceDao;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 获得城市id
     * @param provinceId
     * @return
     */
    public static CityDao getCityId(Integer provinceId){
        Map map = new HashMap(2);
        map.put("token", login.token);
        map.put("provinceId", provinceId);
        try {
            HttpRequest request = new HttpRequest(v1.getCityApi, "POST")
                    .header("Content-Type", "application/json")
                    .send(JSON.toJSONString(map));
            String res = request.body();
            Log.d(">>>>>", "市的数据: "+res);
            CityDao getCityIdDao = JSONObject.parseObject(res, CityDao.class);
            return getCityIdDao;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获得行政区id
     * @param cityId
     * @return
     */
    public static DistrictDao getDistrictDao(Integer cityId){
        Map map = new HashMap(2);
        map.put("token", login.token);
        map.put("cityId", cityId);
        try {
            HttpRequest request = new HttpRequest(v1.getDistrictApi, "POST")
                    .header("Content-Type", "application/json")
                    .send(JSON.toJSONString(map));
            String res = request.body();
            Log.d(">>>>>", "行政区的数据: "+res);
            DistrictDao districtDao = JSONObject.parseObject(res, DistrictDao.class);
            return districtDao;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取街道
     * @param districtId
     * @return
     */
    public static StreetDao getStreetDao(Integer districtId){
        Map map = new HashMap(2);
        map.put("token", login.token);
        map.put("districtId", districtId);
        try {
            HttpRequest request = new HttpRequest(v1.getStreetApi, "POST")
                    .header("Content-Type", "application/json")
                    .send(JSON.toJSONString(map));
            String res = request.body();
            Log.d(">>>>>", "街道的数据: "+res);
            StreetDao streetDao = JSONObject.parseObject(res, StreetDao.class);
            return streetDao;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取社区
     * @param streetId
     * @return
     */
    public static CommunityDao getCommunityDao(Integer streetId){
        Map map = new HashMap(2);
        map.put("token", login.token);
        map.put("streetId", streetId);
        try {
            HttpRequest request = new HttpRequest(v1.getCommunityApi, "POST")
                    .header("Content-Type", "application/json")
                    .send(JSON.toJSONString(map));
            String res = request.body();
            Log.d(">>>>>", "街道的数据: "+res);
            CommunityDao communityDao = JSONObject.parseObject(res, CommunityDao.class);
            return communityDao;
        } catch (Exception e) {
            return null;
        }
    }
}
