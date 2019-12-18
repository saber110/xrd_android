package com.example.collectdata;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.collectdata.tools.JsonTools;
import com.example.collectdata_01.R;
import com.example.map.baidu_map.BaiduMapActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class NearByActivity extends AppCompatActivity implements TabHost.TabContentFactory,BaiduMap.OnMapLongClickListener,View.OnClickListener {
    private BaiduMap baiduMap;
    private MapView mMapView = null;
    private TabHost tabHost;
    private Button search,locButton,save,location;
    private TextView textView;
    private LocationClient mLocationClient;
    private boolean isShowLoc = false;
    private final int tab_num = 18;
    private final int set_num = 18;
    private final int r = 1000;
    private LinearLayout[] linearLayout = new LinearLayout[tab_num+1];
    private double latitude = 0.0,longitude = 0.0;
    private String select_tab = "tab1";
    private HashSet<String>[]sets = new HashSet[set_num+1];
    public static HashMap<String,Object> map = new HashMap<>();
    private int busStationDistance = 1000000000;
    private int subwayDistance = 1000000000;
    private ArrayList<Integer> radius;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_near_by);
        mMapView = findViewById(R.id.bmapView2);
        tabHost = (TabHost)findViewById(R.id.myTabHost);
        search = (Button)findViewById(R.id.button_search);
        locButton = (Button)findViewById(R.id.dw_bt2);
        save = (Button)findViewById(R.id.save_button);
        location = (Button)findViewById(R.id.search_location);
        textView = (TextView)findViewById(R.id.text_input_location);
        baiduMap = mMapView.getMap();
        for(int i=1;i<=tab_num;i++){
            linearLayout[i] = new LinearLayout(this);
            linearLayout[i].setOrientation(LinearLayout.VERTICAL);
        }
        for(int i=0;i<=set_num;i++){
            sets[i] = new HashSet<>();
            sets[i].clear();
        }
        tabHost.setup();
        // 加上标签
        // 参数设置：新增的TabSpec的标签，标签中显示的字样
        // setContent设置内容对应的View资源标号
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("相邻小区").setContent(this));
        //tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("交通干道").setContent(this));
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("公交站名").setContent(this));
        tabHost.addTab(tabHost.newTabSpec("tab4").setIndicator("普通公交").setContent(this));
        tabHost.addTab(tabHost.newTabSpec("tab5").setIndicator("快速公交").setContent(this));
        tabHost.addTab(tabHost.newTabSpec("tab6").setIndicator("地铁站名").setContent(this));
        tabHost.addTab(tabHost.newTabSpec("tab7").setIndicator("农贸市场").setContent(this));
        tabHost.addTab(tabHost.newTabSpec("tab8").setIndicator("超市商场").setContent(this));
        tabHost.addTab(tabHost.newTabSpec("tab9").setIndicator("医疗设施").setContent(this));
        tabHost.addTab(tabHost.newTabSpec("tab10").setIndicator("金融机构").setContent(this));
        tabHost.addTab(tabHost.newTabSpec("tab11").setIndicator("文体场馆").setContent(this));
        tabHost.addTab(tabHost.newTabSpec("tab12").setIndicator("行政机关").setContent(this));
        tabHost.addTab(tabHost.newTabSpec("tab13").setIndicator("幼儿园").setContent(this));
        tabHost.addTab(tabHost.newTabSpec("tab14").setIndicator("小学教育").setContent(this));
        tabHost.addTab(tabHost.newTabSpec("tab15").setIndicator("中学教育").setContent(this));
        tabHost.addTab(tabHost.newTabSpec("tab16").setIndicator("大学教育").setContent(this));
        tabHost.addTab(tabHost.newTabSpec("tab17").setIndicator("旅游景点").setContent(this));
        tabHost.addTab(tabHost.newTabSpec("tab18").setIndicator("公园广场").setContent(this));
        search.setOnClickListener(this);
        locButton.setOnClickListener(this);
        save.setOnClickListener(this);
        location.setOnClickListener(this);
        beginLoc();
        radius = getIntent().getIntegerArrayListExtra("radius");
        System.out.println("radius="+radius.size());
//        i.putExtra("map",(Serializable)map);
//        HashMap<Integer, String> map = (HashMap<Integer, String>) getIntent().getSerializableExtra("map");
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_search:
                System.out.println("搜索周边");
                search.setText("搜索中...");
                search.setEnabled(false);
                for(int i=1;i<=tab_num;i++) linearLayout[i].removeAllViews();
                for(int i=1;i<=set_num;i++) sets[i].clear();
                searchNeayBy("小区",1);
                //searchNeayBy("道路",2);
                searchNeayBy("公交站",3);
                searchNeayBy("普通公交",4);
                searchNeayBy("BRT",5);
                searchNeayBy("地铁站",6);
                searchNeayBy("农贸市场",7);
                searchNeayBy("超市商场",8);
                searchNeayBy("医院",9);
                searchNeayBy("银行",10);
                searchNeayBy("体育馆",11);
                searchNeayBy("行政机关",12);
                searchNeayBy("幼儿园",13);
                searchNeayBy("小学",14);
                searchNeayBy("中学",15);
                searchNeayBy("大学",16);
                searchNeayBy("景点",17);
                searchNeayBy("公园",18);
                break;
            case R.id.dw_bt2:
                System.out.println("定位");
                initLocation();
                break;
            case R.id.save_button:
                System.out.println("保存");
                save("neighborGarden",sets[1]);
                //save("mainRoad",sets[2]);
                save("busStation",sets[3]);
                save("baseBus",sets[4]);
                save("quickBus",sets[5]);
                save("subwayStation",sets[6]);
                save("farmerMarket",sets[7]);
                save("market",sets[8]);
                save("hospital",sets[9]);
                save("bank",sets[10]);
                save("gym",sets[11]);
                save("organization",sets[12]);
                save("kindergarten",sets[13]);
                save("primary",sets[14]);
                save("middle",sets[15]);
                save("college",sets[16]);
                save("attractions",sets[17]);
                save("park",sets[18]);
                map.put("busStationDistance",busStationDistance);
                map.put("busLines",sets[4].size()+sets[5].size());
                map.put("subwayDistance",subwayDistance);
                map.put("subwayLines",sets[6].size());
                for(String s:map.keySet()){
                    System.out.println(s+":"+map.get(s));
                }
                Intent i = new Intent();
                i.putExtra("map",map);
                setResult(RESULT_OK, i);
                //HashMap<Integer, String> map = (HashMap<Integer, String>) getIntent().getSerializableExtra("map");
                Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.search_location:
                System.out.println("搜索地点");
                searchLocation();
        }

    }
    //搜索地点
    private void searchLocation(){
        System.out.println("搜索地点");
        PoiSearch poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            // poi 查询结果回调
            @Override
            public void onGetPoiResult(PoiResult result) {
                if (result != null) {
                    if (result.getAllPoi() != null && result.getAllPoi().size() > 0) {
                        PoiInfo poiInfo = result.getAllPoi().get(0);
                        System.out.println(poiInfo);
                        LatLng location = new LatLng(poiInfo.getLocation().latitude,poiInfo.getLocation().longitude);
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.target(location).zoom(18.0f);
                        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    }
                }
            }

            // poi 详情查询结果回调
            @Override
            public void onGetPoiDetailResult(
                    PoiDetailResult poiDetailResult) {
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
        // 城市内检索
        PoiCitySearchOption poiCitySearchOption = new PoiCitySearchOption();
        // 关键字
        poiCitySearchOption.keyword(textView.getText().toString());
        // 城市
        poiCitySearchOption.city(textView.getText().toString());
        // 设置每页容量，默认为每页10条
        poiCitySearchOption.pageCapacity(20);
        // 分页编号
        poiSearch.searchInCity(poiCitySearchOption);
        // 设置poi检索监听者

    }
    private void save(String key,HashSet<String> set){
        try {
            if(set.isEmpty()){
                map.put(key,"");
                return;
            }
            String value = "";
            for (String item : set) {
                value += (item + ",");
            }
            value = value.substring(0, value.length() - 1);
            System.out.println(key + ":" + value);
            map.put(key, value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //创建CheckBox
    private CheckBox newCheckBox(String name){
        final CheckBox checkBox = new CheckBox(getApplicationContext());
        checkBox.setText(name);
        checkBox.setChecked(true);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int select_index = Integer.parseInt(tabHost.getCurrentTabTag().substring(3));
                String text = compoundButton.getText().toString();
                System.out.println("选择第"+select_index+"个页面的"+text);
                if(b) sets[select_index].add(text);
                else sets[select_index].remove(text);
            }
        });
        return checkBox;
    }
    private void searchNeayBy(String key, final int index){
        // POI初始化搜索模块，注册搜索事件监听
        Log.d("keyword",key);
        PoiSearch mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult result) {
                if (result != null) {
                    if (result.getAllPoi()!=null&&result.getAllPoi().size()>0){
                        for(int i=0;i<result.getAllPoi().size();i++){
                            PoiInfo poiInfo = result.getAllPoi().get(i);
                            System.out.println(poiInfo);
                            LatLng gp1=new LatLng(latitude,longitude);
                            LatLng gp2=new LatLng(poiInfo.getLocation().latitude,poiInfo.getLocation().longitude);
                            int distence = (int)DistanceUtil. getDistance(gp1, gp2);
                            String name = poiInfo.name;
                            sets[index].add(name);
                            if(index<=18) {
                                linearLayout[index].addView(newCheckBox(name+"（距离："+distence+"m）"));
                                //公交站距离
                                if(index==3){
                                    //计算最近的公交站距离
                                    if(distence<busStationDistance){
                                        busStationDistance = distence;
                                    }
                                    //获得所有公交车名
                                    String address = poiInfo.getAddress();
                                    String []buses = address.split(";");
                                    for(String bus:buses){
                                        sets[4].add(bus);
                                    }
                                }
                                //地铁站距离
                                if(index==6){
                                    if(distence<subwayDistance){
                                        subwayDistance = distence;
                                    }
                                }
                            }
                        }
                        if(index==3){
                            System.out.println("busStationDistance:"+busStationDistance);
                            sets[4].remove("");
                            for(String bus:sets[4]){
                                linearLayout[4].addView(newCheckBox(bus));
                            }
                        }
                    }
                }
                search.setText("搜索");
                search.setEnabled(true);
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
        PoiNearbySearchOption poiNearbySearchOption = new PoiNearbySearchOption();
        poiNearbySearchOption.keyword(key);
        poiNearbySearchOption.location(new LatLng(latitude, longitude));
        if(index>2) poiNearbySearchOption.radius(radius.get(index-2));
        else poiNearbySearchOption.radius(radius.get(index-1));  // 检索半径，单位是米
        poiNearbySearchOption.pageCapacity(20);  // 默认每页10条
        mPoiSearch.searchNearby(poiNearbySearchOption);  // 发起附近检索请求
    }
    private void initLocation(){
        MyLocationData locationData = baiduMap.getLocationData();
        LatLng ll = new LatLng(locationData.latitude, locationData.longitude);
        latitude = locationData.latitude;
        longitude = locationData.longitude;
        Log.i("location111","latitude:"+locationData.latitude+" , longgitude:"+locationData.longitude);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(10.0f);
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        drawCircle();
    }
    private void drawCircle(){
        baiduMap.clear();
        LatLng pt = new LatLng(latitude, longitude);
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(pt);                          //设置圆心坐标
        circleOptions.fillColor(0xAAFFFFFF);               //圆填充颜色
        circleOptions.radius(r);                         //设置半径
        circleOptions.stroke(new Stroke(5, 0xAA00FF00));   // 设置边框
        baiduMap.addOverlay(circleOptions);
    }
    @Override
    public View createTabContent(String tag)
    {
// 参数： 这个方法会接受到被选择的tag的标签
        Log.d("select_tab:",select_tab);
        ScrollView scrollView = new ScrollView(this);
        for(int i=1;i<=tab_num;i++){
            if(tag.equals("tab"+i)){
                scrollView.addView(linearLayout[i]);
                break;
            }
        }
        return scrollView;
    }
    private void beginLoc() {
        //定位初始化
        mLocationClient = new LocationClient(this);
        baiduMap.setOnMapLongClickListener(this);
        baiduMap.setMyLocationEnabled(true);
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        // 打开gps
        option.setOpenGps(true);
        option.setScanSpan(5000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenAutoNotifyMode(1000, 0, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        //设置locationClientOption
        mLocationClient.setLocOption(option);
        //注册LocationListener监听器
        mLocationClient.registerLocationListener(new MyLocationListener());
        //开启地图定位图层
        mLocationClient.start();
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
            latitude = locData.latitude;
            longitude = locData.longitude;
            if (isShowLoc) {
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
                baiduMap.animateMapStatus(msu);
//                MapStatus.Builder builder = new MapStatus.Builder();
//                builder.target(ll).zoom(18.0f);
//                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                msu = MapStatusUpdateFactory.zoomBy(5f);
                // 放大
                baiduMap.animateMapStatus(msu);
                isShowLoc = false;
            }
        }
    }
    @Override
    public void onMapLongClick(final LatLng latLng) {
        System.out.println("LongClick");
        latitude = latLng.latitude;
        longitude = latLng.longitude;
        drawCircle();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mMapView.onDestroy();
    }
    @Override
    protected void onResume(){
        super.onResume();
        mMapView.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    initLocation();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    protected void onPause(){
        super.onPause();
        mMapView.onPause();
        baiduMap.setMyLocationEnabled(false);
    }
}
