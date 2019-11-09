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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.dialog.ChooseMapDialog;
import com.example.map.baidu_map.BaiduMapActivity;
import com.example.map.dao.AddGradenResult;
import com.example.map.net.AddGarden;
import com.example.map.tecent_map.TecentActivity;
import com.example.net.AsyncRequest;

import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements AMapLocationListener {
    private TextView textView;
    private RelativeLayout mapLayout, relativeLayout;
    private Dialog dialog;
    private int selectMap = 0;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.position);
        relativeLayout = (RelativeLayout) findViewById(R.id.zhaopian);
        mapLayout = (RelativeLayout)findViewById(R.id.ditu);
        view = getLayoutInflater().inflate(R.layout.map_enter_dialog_layout, null);
        dialog = ChooseMapDialog.createDialog(MainActivity.this, view);
        mapLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.show();
                final Spinner spinner = view.findViewById(R.id.choose_map_list);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int pos, long id) {
                        String[] languages = getResources().getStringArray(R.array.map_list);
                        selectMap = pos;
                        Toast.makeText(MainActivity.this, "你点击的是:"+languages[pos], Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Another interface callback
                    }
                });

                view.findViewById(R.id.choose_map_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText = view.findViewById(R.id.choose_garden_name);
                        if (editText.getText().toString() == null && editText.getText().toString().length() == 0) {
                            Toast.makeText(MainActivity.this, "请输入小区名字", Toast.LENGTH_SHORT);
                        } else {
                            AddGarden addGarden = new AddGarden(editText.getText().toString());
                            AsyncTask asyncTask = new AsyncRequest().execute(addGarden);
                            try {
                                String result = (String) asyncTask.get();
                                AddGradenResult addGradenResult = JSONObject.parseObject(result, AddGradenResult.class);
                                if (addGradenResult.getCode() == 0) {
                                    Intent intent;
                                    if (spinner.getSelectedItemPosition() == 0){
                                         intent = new Intent(MainActivity.this, BaiduMapActivity.class);
                                    }else {
                                         intent = new Intent(MainActivity.this, TecentActivity.class);
                                    }
                                    intent.putExtra("gardenId",addGradenResult.getData().getGardenId());
                                    intent.putExtra("selectMapId",spinner.getSelectedItemPosition());
                                    startActivity(intent);
                                }
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }finally {
                                dialog.cancel();
                            }
                        }
                    }
                });
//                startActivity(new Intent(MainActivity.this, AmapActivity.class));
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

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_home_white_24dp, "Home"))
                .addItem(new BottomNavigationItem(R.drawable.ic_book_white_24dp, "Books"))
                .addItem(new BottomNavigationItem(R.drawable.ic_music_note_white_24dp, "Music"))
                .addItem(new BottomNavigationItem(R.drawable.ic_tv_white_24dp, "Movies & TV"))
                .addItem(new BottomNavigationItem(R.drawable.ic_videogame_asset_white_24dp, "Games"))
                .initialise();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
            }
        });
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
