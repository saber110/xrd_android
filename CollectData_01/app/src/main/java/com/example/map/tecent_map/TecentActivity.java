package com.example.map.tecent_map;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.example.collectdata_01.R;
import com.example.collectdata_01.util.BottomUtil;
import com.example.dialog.ChooseMapDialog;
import com.example.map.baidu_map.BaiduMapActivity;
import com.example.map.net.SendMapMsg;
import com.example.net.AsyncRequest;
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

import java.util.concurrent.ExecutionException;


public class TecentActivity extends AppCompatActivity implements TencentMap.OnMapLongClickListener {

    MapView mapview = null;
    private TencentMap tencentMap;
    private String TAG = "腾讯地图";
    private UiSettings uiSettings;
    private LocationListener listener;
    private Dialog dialog;
    private View view;
    private int gardenId;
    private TextView huayuan;
    private TextView louceng;
    private TextView lu;
    private TextView qita;
    private TextView lat;
    private TextView lng;
    private int choose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tecent);
        initChooseMap();
        mapview = findViewById(R.id.tecent_mapview);
        view = getLayoutInflater().inflate(R.layout.send_map_data, null);
        dialog = ChooseMapDialog.createDialog(this, view);
        gardenId = getIntent().getIntExtra("gardenId", 1);

        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        BottomUtil bottomUtil = new BottomUtil(bottomNavigationBar,this,1);
        /**
         * 设置样式添加监听
         */
        bottomUtil.setBottomBarStytle();

        init();
        initLoc();
    }

    /**
     * 设置百度地图腾讯地图和goolemap的颜色
     */
    private void initChooseMap() {

        TextView baiduText = findViewById(R.id.baidu_map);
        TextView tecentText = findViewById(R.id.tecent_map);
        TextView googleText = findViewById(R.id.google_map);
        baiduText.setBackgroundColor(0x99EEE6E6);
        tecentText.setBackgroundColor(Color.RED);
        googleText.setBackgroundColor(0x99EEE6E6);

        /**
         * 点击百度地图进行跳转
         */
        baiduText.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Intent intent = new Intent(TecentActivity.this, BaiduMapActivity.class);
                                             intent.putExtra("gardenId",gardenId);
                                             startActivity(intent);
                                             finish();
                                         }
                                     }
        );

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
        initChoose();
        listener = new LocationListener(this);
        tencentMap = mapview.getMap();
        uiSettings = tencentMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        // 设置卫星地图
        tencentMap.setSatelliteEnabled(true);
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.strokeColor(android.R.color.transparent);
        myLocationStyle.fillColor(android.R.color.transparent);
        tencentMap.setMyLocationStyle(myLocationStyle);
    }

    private void initChoose() {
        huayuan = view.findViewById(R.id.huayuan);
        louceng = view.findViewById(R.id.louceng);
        lu = view.findViewById(R.id.lu);
        qita = view.findViewById(R.id.qita);

        lat = view.findViewById(R.id.lat);
        lng = view.findViewById(R.id.lng);

        huayuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                huayuan.setBackgroundColor(Color.RED);
                louceng.setBackgroundColor(0x99EEE6E6);
                lu.setBackgroundColor(0x99EEE6E6);
                qita.setBackgroundColor(0x99EEE6E6);
                choose = 0;
            }
        });

        louceng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                louceng.setBackgroundColor(Color.RED);
                huayuan.setBackgroundColor(0x99EEE6E6);
                lu.setBackgroundColor(0x99EEE6E6);
                qita.setBackgroundColor(0x99EEE6E6);
                choose = 1;
            }
        });
        lu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lu.setBackgroundColor(Color.RED);
                louceng.setBackgroundColor(0x99EEE6E6);
                huayuan.setBackgroundColor(0x99EEE6E6);
                qita.setBackgroundColor(0x99EEE6E6);
                choose = 2;
            }
        });
        qita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qita.setBackgroundColor(Color.RED);
                louceng.setBackgroundColor(0x99EEE6E6);
                lu.setBackgroundColor(0x99EEE6E6);
                huayuan.setBackgroundColor(0x99EEE6E6);
                choose = 3;
            }
        });
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
     *
     * @param latLng
     */
    @Override
    public void onMapLongClick(final LatLng latLng) {

        dialog.show();
        lat.setText(latLng.latitude + "");
        lng.setText(latLng.longitude + "");
        view.findViewById(R.id.send_map_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = view.findViewById(R.id.input_msg);
                if (name.getText().toString() == null && name.getText().toString().length() == 0) {

                } else {
                    SendMapMsg sendMapMsg = new SendMapMsg(latLng.latitude, latLng.longitude, name.getText().toString(), gardenId, 1, choose);
                    AsyncTask asyncTask = new AsyncRequest().execute(sendMapMsg);
                    try {
                        String result = (String) asyncTask.get();
                        Log.d(">>>>", "onClick: " + result);
                        if (result != null) {
                            Toast.makeText(TecentActivity.this, "发送数据成功", Toast.LENGTH_SHORT).show();
                            tencentMap.addMarker(new MarkerOptions().
                                    position(latLng).
                                    title(name.getText().toString()));
                        }
                    } catch (ExecutionException e) {
                        Toast.makeText(TecentActivity.this, "发送数据失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        Toast.makeText(TecentActivity.this, "发送数据失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } finally {
                        dialog.cancel();
                    }
                }
            }
        });
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
         *
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
                if (flag) {
                    tencentMap.animateCamera(CameraUpdateFactory.zoomTo(17));
                    tencentMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(arg0.getLatitude(), arg0.getLongitude())));
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
}
