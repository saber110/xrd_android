package com.example.map.net;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.collectdata_01.util.DrawUtil;
import com.example.interfaceNet.v1;
import com.example.login.login;
import com.example.map.baidu_map.BaiduMapActivity;
import com.example.map.dao.MapData;
import com.example.map.dao.MapMarkerDataDao;
import com.example.map.dao.StanderDao;
import com.example.map.dao.UploadMarkerReturnDao;
import com.example.map.tecent_map.TecentActivity;
import com.example.net.AsyncRequest;
import com.example.net.ProcessInterface;
import com.github.kevinsawicki.http.HttpRequest;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MarkerNetUtil {

    /**
     * 删除数据
     */
    public static class DeletTecentMarkerUtil extends AsyncTask<ProcessInterface, Void, Object> implements ProcessInterface {

        private Integer id;
        private TecentActivity.DeleteMarkListener deleteMarkListener;

        public DeletTecentMarkerUtil(Integer id, TecentActivity.DeleteMarkListener deleteMarkListener) {
            this.id = id;
            this.deleteMarkListener = deleteMarkListener;
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
                Log.d(">>>>", "call: " + result);
                return JSONObject.parseObject(result, StanderDao.class);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected Object doInBackground(ProcessInterface... processInterfaces) {
            return this.call();
        }

        @Override
        protected void onPostExecute(Object o) {
            StanderDao result = (StanderDao) o;
            if (result != null && "0".equals(result.getCode())) {
                Log.d(">>>", "deleteMark:成功 ");
                deleteMarkListener.deletetMark();
            }
        }
    }

    /**
     * 删除数据
     */
    public static class DeletBaiduMarkerUtil extends AsyncTask<ProcessInterface, Void, Object> implements ProcessInterface {

        private Integer id;
        private BaiduMapActivity.DeleteMarkListener deleteMarkListener;

        public DeletBaiduMarkerUtil(Integer id, BaiduMapActivity.DeleteMarkListener deleteMarkListener) {
            this.id = id;
            this.deleteMarkListener = deleteMarkListener;
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
                Log.d(">>>>", "call: " + result);
                return JSONObject.parseObject(result, StanderDao.class);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected Object doInBackground(ProcessInterface... processInterfaces) {
            return this.call();
        }

        @Override
        protected void onPostExecute(Object o) {
            StanderDao result = (StanderDao) o;
            if (result != null && "0".equals(result.getCode())) {
                Log.d(">>>", "deleteMark:成功 ");
                deleteMarkListener.deletetMark();
            }
        }
    }


    /**
     * 获得腾讯地图mark数据
     */
    public static class GetTecentMarkerData extends AsyncTask<ProcessInterface, Void, Object> implements ProcessInterface {
        private Integer gardenId;
        private int mapId;
        private TencentMap tencentMap;
        private float scale;

        public GetTecentMarkerData(Integer gardenId, int mapId, TencentMap tencentMap, float scale) {
            this.gardenId = gardenId;
            this.mapId = mapId;
            this.tencentMap = tencentMap;
            this.scale = scale;
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

        @Override
        protected Object doInBackground(ProcessInterface... processInterfaces) {
            return this.call();
        }

        @Override
        protected void onPostExecute(Object o) {
            MapMarkerDataDao mapMarkerDataDao = (MapMarkerDataDao) o;
            Log.i("mapMarkerDataDao", "initMark: code " + mapMarkerDataDao.getCode());
            if (mapMarkerDataDao.getCode() == 0) {
                for (int i = 0; i < mapMarkerDataDao.getData().getMap_data().size(); i++) {
                    MapMarkerDataDao.DataBean.MapDataBean mapDataBean = mapMarkerDataDao.getData().getMap_data().get(i);
                    com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions options = new com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions(new com.tencent.tencentmap.mapsdk.maps.model.LatLng(mapDataBean.getLatitude(), mapDataBean.getLongitude())).
                            icon(com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory.fromBitmap(DrawUtil.drawBitMap(mapDataBean.getName(), scale))).tag(mapDataBean.getId());
                    tencentMap.addMarker(options);
                    /**
                     * 镜头移动
                     */
                    if (mapMarkerDataDao.getData().getMap_data().size() != 0) {
                        MapMarkerDataDao.DataBean.MapDataBean data = mapMarkerDataDao.getData().getMap_data().get(0);
                        tencentMap.animateCamera(CameraUpdateFactory.zoomTo(17));
                        tencentMap.animateCamera(CameraUpdateFactory.newLatLng(new com.tencent.tencentmap.mapsdk.maps.model.LatLng(data.getLatitude(), data.getLongitude())));
                    }
                }
            }

        }
    }


    /**
     * 获得百度mark数据
     */
    public static class GetBaiduMarkerData extends AsyncTask<ProcessInterface, Void, Object> implements ProcessInterface {
        private Integer gardenId;
        private int mapId;
        private BaiduMap baiduMap;
        private float scale;

        public GetBaiduMarkerData(Integer gardenId, int mapId, BaiduMap baiduMap, float scale) {
            this.gardenId = gardenId;
            this.mapId = mapId;
            this.baiduMap = baiduMap;
            this.scale = scale;
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

        @Override
        protected Object doInBackground(ProcessInterface... processInterfaces) {
            return this.call();
        }

        @Override
        protected void onPostExecute(Object o) {
            MapMarkerDataDao mapMarkerDataDao = (MapMarkerDataDao) o;
            Log.i("mapMarkerDataDao", "initMark: code " + mapMarkerDataDao.getCode());
            if (mapMarkerDataDao.getCode() == 0) {
                Log.i("mapMarkerDataDao", "initMark: size " + mapMarkerDataDao.getData().getMap_data().size());
                for (int i = 0; i < mapMarkerDataDao.getData().getMap_data().size(); i++) {
                    MapMarkerDataDao.DataBean.MapDataBean mapDataBean = mapMarkerDataDao.getData().getMap_data().get(i);
                    Log.i("mapMarkerDataDao", "initMark: name " + mapDataBean.getName());

                    MarkerOptions options = new MarkerOptions().position(new LatLng(mapDataBean.getLatitude(), mapDataBean.getLongitude())).
                            icon(BitmapDescriptorFactory.fromBitmap((DrawUtil.drawBitMap(mapDataBean.getName(), scale))));
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", mapMarkerDataDao.getData().getMap_data().get(i).getId());
                    options.extraInfo(bundle);
                    baiduMap.addOverlay(options);
                    Log.i("mapMarkerDataDao", "initMark: " + mapDataBean);
                }
                if (mapMarkerDataDao.getData().getMap_data().size() != 0) {
                    MapMarkerDataDao.DataBean.MapDataBean mapDataBean = mapMarkerDataDao.getData().getMap_data().get(0);
                    LatLng ll = new LatLng(mapDataBean.getLatitude(), mapDataBean.getLongitude());
                    MapStatus mMapStatus = new MapStatus.Builder()//定义地图状态
                            .target(ll)
                            .zoom(18)
                            .build(); //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                    baiduMap.setMapStatus(mMapStatusUpdate);//改变地图状态
                }
            }


        }
    }


    /**
     * 添加或则修改数据
     */
    public static class AddTecentMarker extends AsyncTask<ProcessInterface, Void, Object> implements ProcessInterface {

        private MapData mapData = new MapData();
        private TencentMap tencentMap;
        private com.tencent.tencentmap.mapsdk.maps.model.LatLng latLng;
        private String name;
        private float scale;

        public AddTecentMarker(TencentMap tencentMap,com.tencent.tencentmap.mapsdk.maps.model.LatLng latLng, String name, Integer gardenId, int mapId, int kindId, float scale) {
            mapData.setLatitude(latLng.getLatitude());
            mapData.setLongitude(latLng.getLongitude());
            mapData.setKindId(kindId);
            mapData.setName(name);
            mapData.setGardenId(gardenId);
            mapData.setToken(login.token);
            mapData.setMapId(mapId);
            this.name = name;
            this.scale = scale;
            this.tencentMap =tencentMap;
            this.latLng  = latLng;
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
                Log.d(">>>>", "添加: " + result);
                return JSONObject.parseObject(result, UploadMarkerReturnDao.class);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected Object doInBackground(ProcessInterface... processInterfaces) {
            return this.call();
        }

        @Override
        protected void onPostExecute(Object o) {
            UploadMarkerReturnDao result = (UploadMarkerReturnDao) o;
            if (result != null && result.getCode() == 0) {
                com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions options =
                        new com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions(latLng).
                                icon(com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory.
                                        fromBitmap(DrawUtil.drawBitMap(name, scale)));
                options.tag(result.getData().getMapDataId());
                tencentMap.addMarker(options);
            }
        }
    }

    /**
     * 添加或则修改数据
     */
    public static class AddBaiduMarker extends AsyncTask<ProcessInterface, Void, Object> implements ProcessInterface {

        private MapData mapData = new MapData();

        private BaiduMap baiduMap;
        private LatLng latLng;
        private String name;
        private float scale;

        public AddBaiduMarker(BaiduMap baiduMap, LatLng latLng, String name, Integer gardenId, int mapId, int kindId, float scale) {
            mapData.setLatitude(latLng.latitude);
            mapData.setLongitude(latLng.longitude);
            mapData.setKindId(kindId);
            mapData.setName(name);
            mapData.setGardenId(gardenId);
            mapData.setToken(login.token);
            mapData.setMapId(mapId);
            this.name = name;
            this.scale = scale;
            this.latLng = latLng;
            this.baiduMap = baiduMap;
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
                Log.d(">>>>", "添加: " + result);
                return JSONObject.parseObject(result, UploadMarkerReturnDao.class);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected Object doInBackground(ProcessInterface... processInterfaces) {
            return this.call();
        }

        @Override
        protected void onPostExecute(Object o) {
            UploadMarkerReturnDao result = (UploadMarkerReturnDao) o;
            if (result != null && result.getCode() == 0) {
                Log.d(">>>>>>", "addMark: 在百度地图上面添加数据");
                MarkerOptions options = new MarkerOptions().position(latLng).
                        icon(BitmapDescriptorFactory.fromBitmap((DrawUtil.drawBitMap(name, scale))));
                Bundle bundle = new Bundle();
                bundle.putInt("id", result.getData().getMapDataId());
                options.extraInfo(bundle);
                baiduMap.addOverlay(options);
            }

        }
    }
}
