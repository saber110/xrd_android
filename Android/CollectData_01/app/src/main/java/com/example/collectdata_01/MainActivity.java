package com.example.collectdata_01;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.collectdata_01.adapter.GardenListAdapter;
import com.example.collectdata_01.util.BottomUtil;
import com.example.dialog.ChooseMapDialog;
import com.example.map.baidu_map.BaiduMapActivity;
import com.example.map.dao.AddGradenResult;
import com.example.map.dao.SearchGardenResultDao;
import com.example.map.net.AddGarden;
import com.example.map.net.SearchGarden;
import com.example.net.AsyncRequest;

import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements AMapLocationListener {
    RelativeLayout relativeLayout;
    private TextView textView;
    private RelativeLayout mapLayout;
    private Dialog dialog;
    private View view;
    private RecyclerView recyclerView;
    private EditText searchKey;
    private TextView addGardenBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.position);
        relativeLayout = (RelativeLayout) findViewById(R.id.zhaopian);
        mapLayout = (RelativeLayout) findViewById(R.id.ditu);
        view = getLayoutInflater().inflate(R.layout.map_enter_dialog_layout, null);

        recyclerView =  view.findViewById(R.id.garden_list);
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
                                GardenListAdapter gardenListAdapter = new GardenListAdapter(MainActivity.this, gardenResultDao.getData());
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


        checkPermissions(needPermissions);
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

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
    };


    /**
     * 检测权限
     * 地图sdk所必需的权限
     *
     * @param needPermissions
     */
    private void checkPermissions(String[] needPermissions) {
        // 假如手机的版本大于23 ， 则申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : needPermissions) {
                // 假如已经授权
                if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                    continue;
                } else {
                    ActivityCompat.requestPermissions(this, needPermissions, 1);
                }
            }
        }
        // 假如手机版本低于23
        else {
            Log.d("授权tag", "checkPermissions: 手机版本低于23");
        }
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Log.d("地址", "onLocationChanged: " + aMapLocation);
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            textView.setText(aMapLocation.getAddress());
        }
    }

}
