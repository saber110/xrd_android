package com.example.map.baidu_map;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.collectdata_01.R;
import com.example.dialog.CreatDialog;
import com.example.map.dao.StanderDao;
import com.example.map.net.MarkerNetUtil;
import com.example.map.tecent_map.TecentActivity;

import java.util.concurrent.ExecutionException;

public class BaiduMapActivity extends AppCompatActivity implements BaiduMap.OnMapLongClickListener, View.OnClickListener, BaiduMap.OnMarkerClickListener {

    private MapView mMapView = null;
    private BaiduMap baiduMap;
    private LocationClient mLocationClient;
    private Button locButton;
    private Intent intent;
    private Integer gardenId;
    private View view, changeView;
    private Dialog dialog, changeDialog;
    private TextView huayuan;
    private TextView louceng;
    private TextView lu;
    private TextView qita;
    private TextView lat;
    private TextView lng;
    private int choose = 1;
    private EditText name;
    private float scale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_baidu_map);
        scale = this.getResources().getDisplayMetrics().density;
        mMapView = findViewById(R.id.bmapView);
        /**
         * dialog 的view
         */
        view = getLayoutInflater().inflate(R.layout.send_map_data, null);
        changeView = getLayoutInflater().inflate(R.layout.change_mark_data, null);
        dialog = CreatDialog.createSendMapDataDialog(this, view);
        changeDialog = CreatDialog.createSendMapDataDialog(this, changeView);
        intent = getIntent();
        gardenId = intent.getIntExtra("gardenId", 1);
        initChooseMap();
        initChoose();
        initMap();
        beginLoc();
        initMark();
    }

    /**
     * 初始化marker
     */
    private void initMark() {
        // 1 代表百度地图
        MarkerNetUtil.GetBaiduMarkerData getBaiduMarkerData = new MarkerNetUtil.GetBaiduMarkerData(gardenId, 1, baiduMap, scale);
        getBaiduMarkerData.execute();
    }

    /**
     * 设置百度地图腾讯地图和goolemap的颜色
     */
    private void initChooseMap() {

        TextView baiduText = findViewById(R.id.baidu_map);
        TextView tecentText = findViewById(R.id.tecent_map);
        baiduText.setBackgroundColor(Color.RED);
        tecentText.setBackgroundColor(0x99EEE6E6);

        /**
         * 点击百度地图进行跳转
         */
        tecentText.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              Intent intent = new Intent(BaiduMapActivity.this, TecentActivity.class);
                                              intent.putExtra("gardenId", gardenId);
                                              startActivity(intent);
                                              finish();
                                          }
                                      }
        );

    }


    private void initChoose() {

//        huayuan = view.findViewById(R.id.huayuan);
//        louceng = view.findViewById(R.id.louceng);
//        lu = view.findViewById(R.id.lu);
//        qita = view.findViewById(R.id.qita);
//
        lat = view.findViewById(R.id.lat);
        lng = view.findViewById(R.id.lng);
//
//        huayuan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                huayuan.setBackgroundColor(Color.RED);
//                louceng.setBackgroundColor(0x99EEE6E6);
//                lu.setBackgroundColor(0x99EEE6E6);
//                qita.setBackgroundColor(0x99EEE6E6);
//                choose = 1;
//            }
//        });
//
//        louceng.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                louceng.setBackgroundColor(Color.RED);
//                huayuan.setBackgroundColor(0x99EEE6E6);
//                lu.setBackgroundColor(0x99EEE6E6);
//                qita.setBackgroundColor(0x99EEE6E6);
//                choose = 2;
//            }
//        });
//        lu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                lu.setBackgroundColor(Color.RED);
//                louceng.setBackgroundColor(0x99EEE6E6);
//                huayuan.setBackgroundColor(0x99EEE6E6);
//                qita.setBackgroundColor(0x99EEE6E6);
//                choose = 3;
//            }
//        });
//        qita.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                qita.setBackgroundColor(Color.RED);
//                louceng.setBackgroundColor(0x99EEE6E6);
//                lu.setBackgroundColor(0x99EEE6E6);
//                huayuan.setBackgroundColor(0x99EEE6E6);
//                choose = 4;
//            }
//        });
    }

    private void beginLoc() {
        //定位初始化
        mLocationClient = new LocationClient(this);
        baiduMap.setOnMapLongClickListener(this);
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        // 打开gps
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenAutoNotifyMode(1000, 0, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        option.setCoorType("bd09ll");
        //设置locationClientOption
        mLocationClient.setLocOption(option);
        //注册LocationListener监听器
        mLocationClient.registerLocationListener(new MyLocationListener());
        //开启地图定位图层
        mLocationClient.start();
    }

    private boolean isNormalMap = true;

    private void initMap() {
        locButton = findViewById(R.id.dw_bt);
        locButton.setOnClickListener(this);
        baiduMap = mMapView.getMap();
        baiduMap.setOnMarkerClickListener(this);
        /**
         * 设置为普通3d地图
         */
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baiduMap.setMyLocationEnabled(true);
        baiduMap.getUiSettings().setRotateGesturesEnabled(false);
        findViewById(R.id.change_baidu_map_style).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNormalMap = !isNormalMap;
                if (isNormalMap) {
                    /**
                     * 设置为普通3d地图
                     */
                    baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                } else {
                    // 设置为卫星地图
                    baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                }
            }
        });
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
        name = view.findViewById(R.id.input_msg);
        dialog.show();
        lat.setText((latLng.latitude + "").substring(0, 7));
        lng.setText((latLng.longitude + "").substring(0, 7));
        view.findViewById(R.id.send_map_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().length() == 0) {
                    Toast.makeText(BaiduMapActivity.this, "请输入数据", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    addMark(latLng);
                    name.setText(null);
                }
            }
        });

    }

    private boolean isShowLoc = false;

    @Override
    public void onClick(View view) {
        MyLocationData locationData = baiduMap.getLocationData();
        LatLng ll = new LatLng(locationData.latitude, locationData.longitude);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(18.0f);
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        changeDialog.show();
        name = changeView.findViewById(R.id.change_msg);
        changeView.findViewById(R.id.delete_data_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeDialog.dismiss();
                deleteMark(marker,marker.getExtraInfo().getInt("id"));
            }
        });

        changeView.findViewById(R.id.change_data_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "请输入修改数据", Toast.LENGTH_SHORT).show();
                } else {
                    changeDialog.dismiss();
                    deleteMark(marker,marker.getExtraInfo().getInt("id"));
                    addMark(marker.getPosition());
                    name.setText(null);

                }
            }
        });
        return true;
    }

    private void addMark(LatLng latLng) {
        Log.d(">>>", "新添加的数据" + name.getText().toString());
        MarkerNetUtil.AddBaiduMarker sendMapMsg = new MarkerNetUtil.AddBaiduMarker(baiduMap, latLng, name.getText().toString(), gardenId, 1, choose, scale);
        sendMapMsg.execute();
    }

    /**
     * 删除marker
     *
     * @return
     */
    private void deleteMark(Marker marker,Integer id) {
        MarkerNetUtil.DeletBaiduMarkerUtil deletBaiduMarkerUtil = new MarkerNetUtil.DeletBaiduMarkerUtil(id, new DeleteMarkListener(marker));
        deletBaiduMarkerUtil.execute();
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

    public class DeleteMarkListener {
        private Marker marker;

        public DeleteMarkListener(Marker marker) {
            this.marker = marker;
        }

        public void deletetMark() {
            marker.remove();
        }
    }
}

