package com.example.map.aMap;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collectdata_01.R;
import com.example.net.AsyncRequest;
import com.example.net.ProcessInterface;
import com.github.kevinsawicki.http.HttpRequest;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.LocationSource;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.UiSettings;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.MyLocationStyle;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class TecentActivity extends AppCompatActivity implements TencentMap.OnMapLongClickListener {

    MapView mapview = null;
    private TencentMap tencentMap;
    private String TAG = "腾讯地图";
    private UiSettings uiSettings;
    private LocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tecent);
        mapview = findViewById(R.id.tecent_mapview);
        Button button =findViewById(R.id.choose_amap);
        button.setText("跳转到高德地图");
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TecentActivity.this,AmapActivity.class);
                startActivity(intent);
                finish();
            }
        });
        init();
        initLoc();

    }

    /**
     * 初始化定位
     */
    private void initLoc() {
        tencentMap.setLocationSource(listener);
        tencentMap.setMyLocationEnabled(true);
        tencentMap.setOnMapLongClickListener(this);
    }

    /**
     * 初始化腾讯地图配置
     */
    private void init() {

        listener = new LocationListener(this);
        tencentMap = mapview.getMap();
        uiSettings = tencentMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.strokeColor(android.R.color.transparent);
        myLocationStyle.fillColor(android.R.color.transparent);
        tencentMap.setMyLocationStyle(myLocationStyle);
    }


    @Override
    protected void onStart() {
//  TODO Auto-generated method stub
        super.onStart();
        mapview.onStart();
    }

    @Override
    protected void onResume() {
// TODO Auto-generated method stub
        super.onResume();
        mapview.onResume();
    }

    @Override
    protected void onPause() {
// TODO Auto-generated method stub
        super.onPause();
        mapview.onPause();
    }

    @Override
    protected void onStop() {
// TODO Auto-generated method stub
        super.onStop();
        mapview.onStop();
    }

    @Override
    protected void onRestart() {
// TODO Auto-generated method stub
        super.onRestart();
        mapview.onRestart();
    }

    @Override
    protected void onDestroy() {
// TODO Auto-generated method stub
        super.onDestroy();
        mapview.onDestroy();
    }

    /**
     * 长按监听器
     * @param latLng
     */
    @Override
    public void onMapLongClick(LatLng latLng) {
        tencentMap.addMarker(new MarkerOptions().
                position(latLng).
                title("标记地点"));

        AsyncTask asyncTask = new AsyncRequest().execute(new TecentActivity.SendMsgToNet(latLng));
        try {
            String result = (String) asyncTask.get();
            if (result != null){
                Log.d("腾讯地图上传数据", "onMapLongClick: 数据发送成功");
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    class LocationListener implements LocationSource, TencentLocationListener {

        private Context mContext;
        private OnLocationChangedListener mChangedListener;
        private TencentLocationManager locationManager;
        private TencentLocationRequest locationRequest;
        private boolean flag = true;

        public LocationListener(Context context) {
            mContext = context;
            locationManager = TencentLocationManager.getInstance(mContext);
            locationRequest = TencentLocationRequest.create()
                    .setInterval(1000 * 2) // 定位周期 (毫秒)
                    .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_POI) // 定位要求水准
                    .setAllowCache(true); // 是否使用缓存

        }

        /**
         * 如果位置发生改变
         * @param arg0
         * @param arg1
         * @param arg2
         */
        @Override
        public void onLocationChanged(TencentLocation arg0, int arg1,
                                      String arg2) {
            if (arg1 == TencentLocation.ERROR_OK && mChangedListener != null) {
                Location location = new Location(arg0.getProvider());
                location.setLatitude(arg0.getLatitude());
                location.setLongitude(arg0.getLongitude());
                location.setAccuracy(arg0.getAccuracy());

                // 定位 sdk 只有 gps 返回的值才有可能获取到偏向角
                location.setBearing(arg0.getBearing());
                if (flag){
                    tencentMap.animateCamera(CameraUpdateFactory.zoomTo(17));
                    tencentMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(arg0.getLatitude(),arg0.getLongitude())));
                    flag = false;
                }
                mChangedListener.onLocationChanged(location);
            }
        }

        @Override
        public void onStatusUpdate(String arg0, int arg1, String arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void activate(OnLocationChangedListener arg0) {
            // TODO Auto-generated method stub
            mChangedListener = arg0;
            int err = locationManager.requestLocationUpdates(locationRequest, this);
            switch (err) {
                case 1:
                    setTitle("设备缺少使用腾讯定位服务需要的基本条件");
                    break;
                case 2:
                    setTitle("manifest 中配置的 key 不正确");
                    break;
                case 3:
                    setTitle("自动加载libtencentloc.so失败");
                    break;

                default:
                    break;
            }
        }

        @Override
        public void deactivate() {
            // TODO Auto-generated method stub
            locationManager.removeUpdates(this);
            mContext = null;
            locationManager = null;
            locationRequest = null;
            mChangedListener = null;
        }

    }

    private class SendMsgToNet implements ProcessInterface {

        private LatLng latLng;
        public SendMsgToNet(LatLng latLng) {
            this.latLng = latLng;
        }

        @Override
        public Object call() {
            Map map = new HashMap(2);
            map.put("lat", latLng.latitude);
            map.put("long", latLng.longitude);

            // 网址请求位https ，如果不是https则会产生安全问题，需要配置文件解决
            HttpRequest request = new HttpRequest("http://rap2api.taobao.org/app/mock/234350/data/map","POST").form(map);

            // 下面可以将数据进行model化，但是需要根据返回的数据来确定
            //            ResponseDao responseDao = JSONObject.parseObject(request.body(), ResponseDao.class);
            //            return responseDao.getSuccess();
            return request.body();
        }
    }
}
