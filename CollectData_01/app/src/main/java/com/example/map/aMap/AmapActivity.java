package com.example.map.aMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.collectdata_01.R;
import com.example.map.tecent_map.TecentActivity;

public class AmapActivity extends AppCompatActivity implements AMapLocationListener, LocationSource, AMap.OnMapLongClickListener {

    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private boolean isFirstLoc = true;
    private OnLocationChangedListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amap);
        initAct();
        mapView.onCreate(savedInstanceState);
        initMap();
        beginLocation();
        Button button =findViewById(R.id.choose_tecent);
        button.setText("跳转到腾讯地图");
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AmapActivity.this, TecentActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initAct() {
        mapView = this.findViewById(R.id.a_map);
        aMap = mapView.getMap();
    }


    /**
     * 开始定位
     */
    private void beginLocation() {
        mLocationClient.setLocationListener(this);
        //启动定位
        mLocationClient.startLocation();
        aMap.setOnMapLongClickListener(this);
    }

    /**
     * 初始化地图配置
     */
    private void initMap() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

        //设置显示定位按钮 并且可以点击
        UiSettings settings = aMap.getUiSettings();
        //通过aMap对象设置定位数据源的监听
        aMap.setLocationSource(this);
        //显示默认的定位按钮
        settings.setMyLocationButtonEnabled(true);
        settings.setZoomControlsEnabled(false);
        // 定位的蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(1000);
        //连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);

        myLocationStyle.radiusFillColor(android.R.color.transparent);
        myLocationStyle.strokeColor(android.R.color.transparent);
        aMap.setMyLocationStyle(myLocationStyle);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMyLocationEnabled(true);
    }

    /**
     * 定位监听器
     * 前面设置位2000ms监听一次
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                if (isFirstLoc){
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    mListener.onLocationChanged(aMapLocation);
                }

            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
//        aMap.addMarker(new MarkerOptions().position(latLng).title("标记地点"));
//        SendMsg sendMsg = new SendMsg(latLng.latitude, latLng.longitude);
//
//        AsyncTask asyncTask = new AsyncRequest().execute(sendMsg);
//        try {
//            if (asyncTask.get() == null) {
//                Toast.makeText(this, "发送数据失败", Toast.LENGTH_SHORT);
//            }
//        } catch (Exception e) {
//            Toast.makeText(this, "发送数据失败", Toast.LENGTH_SHORT);
//        }

    }
}
