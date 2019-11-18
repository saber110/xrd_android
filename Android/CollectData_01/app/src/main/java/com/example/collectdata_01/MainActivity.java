package com.example.collectdata_01;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.example.collectdata_01.adapter.GardenListAdapter;
import com.example.collectdata_01.util.BottomUtil;
import com.example.dialog.ChooseMapDialog;
import com.example.map.baidu_map.BaiduMapActivity;
import com.example.map.dao.AddGradenResult;
import com.example.map.dao.SearchGardenResultDao;
import com.example.map.net.AddGarden;
import com.example.map.net.SearchGarden;
import com.example.net.AsyncRequest;
import com.google.android.material.card.MaterialCardView;

import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements AMapLocationListener {
    RelativeLayout relativeLayout;
    private MaterialCardView neighbourChose;
    private RelativeLayout mapLayout;
    private Dialog dialog;
    private View view;
    private RecyclerView recyclerView;
    private EditText searchKey;
    private TextView neighbourWorking;
    private TextView addGardenBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        neighbourChose = findViewById(R.id.id_neighbour_chose);
        neighbourWorking = findViewById(R.id.id_neighbour_working);
        relativeLayout = (RelativeLayout) findViewById(R.id.zhaopian);
        mapLayout = (RelativeLayout) findViewById(R.id.ditu);
        view = getLayoutInflater().inflate(R.layout.map_enter_dialog_layout, null);

        recyclerView = view.findViewById(R.id.garden_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        /**
         * 获得弹出来的框框
         */
        dialog = ChooseMapDialog.createDialog(MainActivity.this, view);
        searchKey = view.findViewById(R.id.search_garden_key);
        addGardenBtn = view.findViewById(R.id.add_garden);

        mapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, BaiduMapActivity.class);
                startActivity(intent);
            }
        });
        neighbourChose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            dialog.show();
            /**
             * 当用户点击了搜索
             */
            view.findViewById(R.id.search_garden_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                SearchGarden searchGarden = new SearchGarden("token");
                AsyncTask asyncTask = new AsyncRequest().execute(searchGarden);
                try {
                    SearchGardenResultDao gardenResultDao = (SearchGardenResultDao) asyncTask.get();
                    if (gardenResultDao == null || gardenResultDao.getData() == null
                            || gardenResultDao.getData().getBuildingKinds().size() == 0
                    ) {
                        Toast.makeText(MainActivity.this, "无查询结果", Toast.LENGTH_SHORT).show();
                    } else {
                        GardenListAdapter gardenListAdapter = new GardenListAdapter(MainActivity.this, gardenResultDao.getData(), neighbourWorking);
                        recyclerView.setAdapter(gardenListAdapter);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                }
            });

            addGardenBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                String key = searchKey.getText().toString();
                if (key.trim().length() == 0) {
                    Toast.makeText(MainActivity.this, "请输入小区名字", Toast.LENGTH_SHORT).show();
                } else {
                    AddGarden addGarden = new AddGarden(key);
                    AsyncTask asyncTask = new AsyncRequest().execute(addGarden);
                    try {
                        AddGradenResult addGradenResult  = (AddGradenResult) asyncTask.get();
                        if (addGradenResult.getCode() == 0) {
                            Toast.makeText(MainActivity.this,addGradenResult.getMessage(),Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, BaiduMapActivity.class);
                            intent.putExtra("gardenId",addGradenResult.getData().getGardenId());
                            startActivity(intent);
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                    }
                }

                }
            });
            }
        });


        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            //监听时间，页面跳转
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, takePhoto01.class));
            }
        });

        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        // 第一个参数是导航，第二个参数是this，第三个参数代表这个是哪一个activity的位置，这个与之前的对应
        BottomUtil bottomUtil = new BottomUtil(bottomNavigationBar,this,1000);
        /**
         * 设置样式添加监听
         */
        bottomUtil.setBottomBarStytle();
        getLocPosition();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private AMapLocationClient mapLocationClient;
    public AMapLocationClientOption mLocationOption = null;

    /**
     * 获得此时的定位
     * 通过高德地图获得
     */
    private void getLocPosition() {
        mapLocationClient = new AMapLocationClient(getApplicationContext());
        mapLocationClient.setLocationListener(this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //给定位客户端对象设置定位参数
        mapLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mapLocationClient.startLocation();
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.d("地址", "onLocationChanged: " + aMapLocation);
    }

    public void dialogCancel(){
        dialog.cancel();
    }
}
