package com.example.map.baidu_map;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.collectdata_01.R;
import com.example.dialog.ChooseMapDialog;
import com.example.map.net.SendMapMsg;
import com.example.net.AsyncRequest;

import java.util.concurrent.ExecutionException;

public class BaiduMapActivity extends AppCompatActivity implements BaiduMap.OnMapLongClickListener, View.OnClickListener {

    private MapView mMapView = null;
    private BaiduMap baiduMap;
    private LocationClient mLocationClient;
    private Button locButton;
    private Intent intent;
    private int gradenId;
    private View view;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_baidu_map);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        mMapView = findViewById(R.id.bmapView);
        view = getLayoutInflater().inflate(R.layout.send_map_data, null);
        dialog = ChooseMapDialog.createDialog(this, view);
        intent = getIntent();
        gradenId = intent.getIntExtra("gardenId", 1);
        initMap();
        beginLoc();
    }

    private void beginLoc() {
        //定位初始化
        mLocationClient = new LocationClient(this);
        baiduMap.setOnMapLongClickListener(this);
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        // 打开gps
        option.setOpenGps(true);
        option.setScanSpan(1000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09lll");
        option.setIsNeedAddress(true);
        option.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);

        //设置locationClientOption
        mLocationClient.setLocOption(option);
        //注册LocationListener监听器
        mLocationClient.registerLocationListener(new MyLocationListener());
        //开启地图定位图层
        mLocationClient.start();
    }

    private void initMap() {
        locButton = findViewById(R.id.dw_bt);

        locButton.setOnClickListener(this);

        baiduMap = mMapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        baiduMap.setMyLocationEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    /**
     * 长按
     *
     * @param latLng
     */
    @Override
    public void onMapLongClick(final LatLng latLng) {
        dialog.show();
        view.findViewById(R.id.send_map_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("发送数据据", "onClick: ");
                Log.d("发送数据", "onClick: ");
                EditText name = view.findViewById(R.id.map_name);
                EditText category = view.findViewById(R.id.map_category);
                if (name.getText().toString() == null && name.getText().toString().length() == 0 && category.getText().toString() == null && category.getText().toString().length() == 0) {
                    Toast.makeText(BaiduMapActivity.this, "请输入数据", Toast.LENGTH_SHORT).show();
                } else {
                    SendMapMsg sendMapMsg = new SendMapMsg(latLng.latitude, latLng.longitude, name.getText().toString(), gradenId, 1);
                    AsyncTask asyncTask = new AsyncRequest().execute(sendMapMsg);
                    try {
                        String result = (String) asyncTask.get();
                        Log.d(">>>>", "onClick: "+result);
                        if (result != null) {
                            Toast.makeText(BaiduMapActivity.this, "发送数据成功", Toast.LENGTH_SHORT).show();
                            //构造MarkerOptions对象
                            MarkerOptions ooD = new MarkerOptions()
                                    .position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.baidumark));
                            baiduMap.addOverlay(ooD);
                        }
                    } catch (ExecutionException e) {
                        Toast.makeText(BaiduMapActivity.this, "发送数据失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        Toast.makeText(BaiduMapActivity.this, "发送数据失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }finally {
                        dialog.hide();
                    }
                }
            }
        });

    }

    private boolean isShowLoc = true;

    @Override
    public void onClick(View view) {
        MyLocationData locationData = baiduMap.getLocationData();
        LatLng ll = new LatLng(locationData.latitude, locationData.longitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(msu);
    }


    /**
     * 当位置发生改变的时候
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
            if (isShowLoc) {
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
                baiduMap.animateMapStatus(msu);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                isShowLoc = false;
            }
        }

    }
}

