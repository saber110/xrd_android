package com.example.collectdata_01;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.example.collectdata.DataActivity;
import com.example.collectdata.tools.IntentTools;
import com.example.collectdata_01.adapter.GardenListAdapter;
import com.example.collectdata_01.adapter.GetCityViewAdapter;
import com.example.collectdata_01.adapter.GetCommunityViewAdapter;
import com.example.collectdata_01.adapter.GetDistrictViewAdapter;
import com.example.collectdata_01.adapter.GetProvinceViewAdapter;
import com.example.collectdata_01.adapter.GetStreetViewAdapter;
import com.example.collectdata_01.util.BottomUtil;
import com.example.collectdata_01.util.UploadImgUtil;
import com.example.dialog.CreatDialog;
import com.example.login.login;
import com.example.map.baidu_map.BaiduMapActivity;
import com.example.map.dao.AddGradenResult;
import com.example.map.dao.CityDao;
import com.example.map.dao.CommunityDao;
import com.example.map.dao.DistrictDao;
import com.example.map.dao.LocationAllDao;
import com.example.map.dao.ProvinceDao;
import com.example.map.dao.SearchGardenResultDao;
import com.example.map.dao.StreetDao;
import com.example.map.net.AddGarden;
import com.example.map.net.GetLocationNetUtil;
import com.example.map.net.SearchGarden;
import com.example.net.AsyncRequest;
import com.google.android.material.card.MaterialCardView;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;
import com.litesuits.orm.db.assit.QueryBuilder;

import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.model.TImage;
import org.devio.takephoto.model.TResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class MainActivity extends TakePhotoActivity{
    private RelativeLayout photoLayout;
    private RelativeLayout updataLayout;
    private MaterialCardView neighbourChose;
    private RelativeLayout mapLayout;
    private Dialog gardenDialog;
    private Dialog locationDialog;
    private View selectGardenView;
    private View selectLocationView;
    private RecyclerView selectLocationRecycleView;
    private RecyclerView gardenDataRecyclerView;

    private EditText searchKey;
    private TextView neighbourWorking;
    private TextView addGardenBtn;
    private RelativeLayout dataCollectLayout;
    public static LocationAllDao locationAllDao = new LocationAllDao();
    private String gardenName;
    private static Integer gardenId;
    private static String buildingId;

    //定义了一些参数
    private int yourChoice;
    private String item;
    private Intent intent;
    private String pictureKind;

    public String locationString;
    public String jpegName;
    private String loudong;

    /**
     * 数据库引用对象
     */
    static DataBase mainDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        neighbourChose = findViewById(R.id.id_neighbour_chose);
        neighbourWorking = findViewById(R.id.id_neighbour_working);
        photoLayout = (RelativeLayout) findViewById(R.id.zhaopian);
        mapLayout = (RelativeLayout) findViewById(R.id.ditu);
        updataLayout = (RelativeLayout) findViewById(R.id.shangchuan);

        //设置dialog的样式
        selectGardenView = getLayoutInflater().inflate(R.layout.map_enter_dialog_layout, null);
        selectLocationView = getLayoutInflater().inflate(R.layout.select_city_dialog, null);

        gardenDataRecyclerView = selectGardenView.findViewById(R.id.garden_list);
        selectLocationRecycleView = selectLocationView.findViewById(R.id.select_city_recycleview);

        selectLocationRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        gardenDataRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        /**
         * 获得弹出来的框框
         */
        gardenDialog = CreatDialog.createChangeMarkDialog(MainActivity.this, selectGardenView);
        locationDialog = CreatDialog.createLocationSelectDialog(MainActivity.this, selectLocationView);

        searchKey = selectGardenView.findViewById(R.id.search_garden_key);
        addGardenBtn = selectGardenView.findViewById(R.id.add_garden);
        setBuildingId(null);

        mapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gardenName != null && !gardenName.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, BaiduMapActivity.class);
                    intent.putExtra("gardenId", MainActivity.getGardenId());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "请选择小区", Toast.LENGTH_LONG).show();
                }
            }
        });

        /**
         * 工作小区选择按钮
         */
        neighbourChose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gardenDialog.show();
                /**
                 * 当用户点击了搜索
                 */
                selectGardenView.findViewById(R.id.search_garden_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String key = searchKey.getText().toString();
                        if (key.trim().length() == 0) {
                            Toast.makeText(MainActivity.this, "请输入小区名字", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SearchGarden searchGarden = new SearchGarden(key);
                        AsyncTask asyncTask = new AsyncRequest().execute(searchGarden);
                        try {
                            final SearchGardenResultDao gardenResultDao = (SearchGardenResultDao) asyncTask.get();
                            Log.d("查询结果", "onClick: "+gardenResultDao);
                            if (gardenResultDao == null || gardenResultDao.getCode()!= 0
                                    || gardenResultDao.getData().getGardens().size() == 0
                            ) {
                                Toast.makeText(MainActivity.this, "无查询结果", Toast.LENGTH_SHORT).show();
                            } else {
                                GardenListAdapter adapter = new GardenListAdapter(MainActivity.this, gardenResultDao.getData());
                                adapter.setItemClickListener(new GardenListAdapter.MyItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        SearchGardenResultDao.DataBean.GardensBean bean = gardenResultDao.getData().getGardens().get(position);
                                        setGardenId(bean.getGardenId());
                                        gardenName = bean.getGardenName();
                                        neighbourWorking.setText(gardenName);
                                        gardenDialog.dismiss();
                                    }
                                });
                                gardenDataRecyclerView.setAdapter(adapter);
                            }
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

                /**
                 * 点击新建
                 */
                addGardenBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String key = searchKey.getText().toString();
                        gardenName = key;
                        if (key.trim().length() == 0) {
                            Toast.makeText(MainActivity.this, "请输入小区名字", Toast.LENGTH_SHORT).show();
                        } else {
                            neighbourWorking.setText(gardenName);
                            gardenDialog.dismiss();
                            showProvinceDialog();
                        }
                    }
                });
            }
        });


        photoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            //监听时间，页面跳转
            public void onClick(View v) {
                if (gardenName != null && !gardenName.isEmpty()) {
                    showSingleChoiceDialog();
                } else {
                    Toast.makeText(getApplicationContext(), "请选择小区", Toast.LENGTH_LONG).show();
                }
            }
        });

        updataLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gardenName != null && !gardenName.isEmpty()) {
                    startActivity(new Intent(MainActivity.this, Datalist.class));
                } else {
                    Toast.makeText(getApplicationContext(), "请选择小区", Toast.LENGTH_LONG).show();
                }

            }
        });

        dataCollectLayout = (RelativeLayout) findViewById(R.id.shuju);
        dataCollectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                IntentTools.activitySwich(MainActivity.this, DataActivity.class, false);
            }
        });

//        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
//        // 第一个参数是导航，第二个参数是this，第三个参数代表这个是哪一个activity的位置，这个与之前的对应
//        BottomUtil bottomUtil = new BottomUtil(bottomNavigationBar, this, 1000);
//        /**
//         * 设置样式添加监听
//         */
//        bottomUtil.setBottomBarStytle();

        //页面被创建时就生成数据库
        if (mainDB == null) {
            // 创建数据库,传入当前上下文对象和数据库名称
            mainDB = LiteOrm.newSingleInstance(this, "imageData.db");
        }
        Intent intent11 = getIntent();
        String flagMessage = intent11.getStringExtra("flag");
        if (flagMessage != null) {
            showSingleChoiceDialog();
        }
    }

    /**
     * 展示省选择的按钮
     */
    private void showProvinceDialog() {

        GetLocationNetUtil.GetProvinceNetUtil getProvinceNetUtil = new GetLocationNetUtil.GetProvinceNetUtil();
        AsyncTask asyncTask = new AsyncRequest().execute(getProvinceNetUtil);
        try {
            final ProvinceDao provinceDao = (ProvinceDao) asyncTask.get();
            GetProvinceViewAdapter adapter = new GetProvinceViewAdapter(MainActivity.this,
                    provinceDao.getData());
            adapter.setItemClickListener(new GetProvinceViewAdapter.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    MainActivity.locationAllDao.setProvinceId(provinceDao.getData().getProvinces().get(position).getId());
                    showCityDialog();
                }
            });
            selectLocationRecycleView.setAdapter(adapter);
            locationDialog.show();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showCityDialog() {
        GetLocationNetUtil.GetCityNetUtil getCityNetUtil = new GetLocationNetUtil.GetCityNetUtil(locationAllDao.getProvinceId());
        AsyncTask asyncTask = new AsyncRequest().execute(getCityNetUtil);
        try {
            final CityDao cityDao = (CityDao) asyncTask.get();
//            cityDao.getData();
            GetCityViewAdapter adapter = new GetCityViewAdapter(MainActivity.this,
                    cityDao.getData());
            adapter.setItemClickListener(new GetCityViewAdapter.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    MainActivity.locationAllDao.setCityId(cityDao.getData().getCities().get(position).getId());
                    showDistrict();
                }
            });
            selectLocationRecycleView.setAdapter(adapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void showDistrict() {
        GetLocationNetUtil.GetDistrictNetUtil getDistrictNetUtil = new GetLocationNetUtil.GetDistrictNetUtil(locationAllDao.getCityId());
        AsyncTask asyncTask = new AsyncRequest().execute(getDistrictNetUtil);
        try {
            final DistrictDao districtDao = (DistrictDao) asyncTask.get();
            GetDistrictViewAdapter adapter = new GetDistrictViewAdapter(MainActivity.this,
                    districtDao.getData());
            adapter.setItemClickListener(new GetDistrictViewAdapter.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    MainActivity.locationAllDao.setDistrictId(districtDao.getData().getDistricts().get(position).getId());
                    showStreet();
                }
            });
            selectLocationRecycleView.setAdapter(adapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void showStreet() {
        GetLocationNetUtil.GetStreetNetUtil getStreetNetUtil = new GetLocationNetUtil.GetStreetNetUtil(locationAllDao.getDistrictId());
        AsyncTask asyncTask = new AsyncRequest().execute(getStreetNetUtil);
        try {
            final StreetDao streetDao = (StreetDao) asyncTask.get();
            GetStreetViewAdapter adapter = new GetStreetViewAdapter(MainActivity.this,
                    streetDao.getData());
            adapter.setItemClickListener(new GetStreetViewAdapter.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    MainActivity.locationAllDao.setStreetId(streetDao.getData().getStreets().get(position).getId());
                    showCommunity();
                }
            });
            selectLocationRecycleView.setAdapter(adapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void showCommunity() {
        GetLocationNetUtil.GetCommunityNetUtil getCommunityNetUtil = new GetLocationNetUtil.GetCommunityNetUtil(locationAllDao.getStreetId());
        AsyncTask asyncTask = new AsyncRequest().execute(getCommunityNetUtil);
        try {
            final CommunityDao communityDao = (CommunityDao) asyncTask.get();
            GetCommunityViewAdapter adapter = new GetCommunityViewAdapter(MainActivity.this, communityDao.getData());
            adapter.setItemClickListener(new GetCommunityViewAdapter.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Log.d(">>>>", "onItemClick: " + position);
                    MainActivity.locationAllDao.setCommunityId(communityDao.getData().getCommunities().get(position).getId());
                    showGarden();
                }
            });
            selectLocationRecycleView.setAdapter(adapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加小区数据
     */
    private void showGarden() {
        AddGarden addGarden = new AddGarden(gardenName, locationAllDao.getProvinceId(), locationAllDao.getCityId(), locationAllDao.getDistrictId()
                , locationAllDao.getStreetId(), locationAllDao.getCommunityId());
        AsyncTask asyncTask = new AsyncRequest().execute(addGarden);
        try {
            AddGradenResult addGradenResult = (AddGradenResult) asyncTask.get();
            gardenId = addGradenResult.getData().getGardenId();
            this.setGardenId(gardenId);
            locationDialog.dismiss();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //以下是重写了方法
    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        showImg(result.getImage());
        Users musers;
        if(getBuildingId() == null)
            musers = new Users(Integer.toString(MainActivity.getGardenId()), locationString, Long.toString(System.currentTimeMillis()), jpegName);
        else
            musers = new Users(getBuildingId(), locationString, Integer.toString((int) System.currentTimeMillis()), jpegName, Integer.toString(MainActivity.getGardenId()));
        mainDB.save(musers);
    }

    String n;

    //普通の方法
    public void formatString(int i) {
        if (i < 10) {
            n = "00" + Integer.toString(i + 1);
        } else if (i >= 100) {
            n = Integer.toString(i + 1);
        } else {
            n = "0" + Integer.toString(i + 1);
        }
    }

    long count; //定义里面类型个数

    //拍照方法
    private void picture() {

        QueryBuilder<Users> qb = new QueryBuilder<Users>(Users.class)
                .columns(new String[]{"pictureKind"})
                .appendOrderAscBy("pictureKind")
                .appendOrderDescBy("pictureKind")
                .distinct(true)
                .where("pictureKind" + "=?", new String[]{locationString});
        count = mainDB.queryCount(qb);

        //保存的文件名，先格式化000字符
        formatString((int) (count++));
        if (locationString.equals("平面图")) {
            jpegName = "2_" + neighbourWorking.getText() + "_平面图_" + n + ".jpg";
            pictureKind = Integer.toString(2);
        }
        if(locationString.equals("小区入口")){
            jpegName = "2_"+ neighbourWorking.getText() + "_小区入口_" + n  + ".jpg";
            pictureKind = Integer.toString(2);
        }
        if(locationString.equals("外景图")){
            jpegName = "2_"+ neighbourWorking.getText() + "_外景图_" + n + ".jpg";
            pictureKind = Integer.toString(2);
        }
        if(locationString.equals("内景图")){
            jpegName = "3_"+ neighbourWorking.getText() + "_内景图_" + n + ".jpg";
            pictureKind = Integer.toString(2);
        }
        if(locationString.equals("幢牌号")){
            jpegName = "3_"+ neighbourWorking.getText()+ loudong + "_幢牌号_" + n + ".jpg";
            pictureKind = Integer.toString(3);
        }
        if(locationString.equals("建筑立面")){
            pictureKind = Integer.toString(3);
            jpegName = "3_"+ neighbourWorking.getText()+ loudong + "_建筑立面_" + n +".jpg";
        }
        if(locationString.equals("其他")){
            pictureKind = Integer.toString(3);
            jpegName = "4_"+ neighbourWorking.getText() + "_其他_" + n +".jpg";
        }

        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + jpegName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        final Uri imageUri = Uri.fromFile(file);
        getTakePhoto().onPickFromCapture(imageUri);

    }

    /**
     * 将拍照的图片加入系统相册中
     *
     * @param path
     */
    private void saveToSystemAlbum(String path) {
        //其次把文件插入到系统图库
        File file = new File(path);
        try {
            Log.i("update album1", "picture: " + file.getAbsolutePath());

            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    file.getAbsolutePath(), jpegName, null);
            Log.i("update album", "picture: " + file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 通知图库更新
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                            sendBroadcast(mediaScanIntent);
                        }
                    });
        } else {
            String relationDir = file.getParent();
            File file1 = new File(relationDir);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file1.getAbsoluteFile())));
        }
    }

    /**
     * 图片种类选择对话框
     */
    private void showSingleChoiceDialog(){
        final String[] items = {"平面图","小区入口","外景图","内景图","建筑立面","幢牌号","其他"};
        yourChoice = -1;
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(MainActivity.this);
        singleChoiceDialog.setTitle("选择你要拍摄的区域");
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yourChoice = which;
                        item = items[yourChoice];
                        locationString = item;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //自定义对话框
                        final Context context = MainActivity.this;
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("请输入楼栋");    //设置对话框标题
                        builder.setIcon(R.drawable.logo);   //设置对话框标题前的图标
                        final EditText edit = new EditText(context);
                        builder.setView(edit);
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setBuildingId(edit.getText().toString());
                                Toast.makeText(context, "你确立的楼栋是: " + edit.getText().toString(), Toast.LENGTH_SHORT).show();
                                //把输入的地点赋给 fu.locationString
                                loudong = edit.getText().toString();
                                //调用拍照方法
                                picture();
                            }
                        });
//                            builder.setPositiveButton("不清楚", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Toast.makeText(context, "遗憾，你不知道这个地方: " + edit.getText().toString(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "你取消输入", Toast.LENGTH_SHORT).show();
                            }
                        });
                        if (yourChoice != -1) {
                            Toast.makeText(MainActivity.this,"你选择了" + items[yourChoice], Toast.LENGTH_SHORT).show();

                            //若选择了立面则立马跳出来自定义对话框
                            if(items[yourChoice].equals("建筑立面") || items[yourChoice].equals("幢牌号")){
                                builder.setCancelable(true);    //设置按钮是否可以按返回键取消,false则不可以取消
                                AlertDialog dialoga = builder.create();  //创建对话框
                                dialoga.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
                                dialoga.show();
                            }
                            else {
                                setBuildingId(null);
                                picture();
                            }
                        }
                        //可能没按选择，默认为平面
                        else {
                            Toast.makeText(MainActivity.this,
                                    "你选择了平面图",
                                    Toast.LENGTH_SHORT).show();
                            locationString = "平面图";
                            picture();
                            setBuildingId(null);

                        }
                    }
                });
        singleChoiceDialog.show();
    }

    /**
     * 图片涂鸦单选对话框
     */
    private void singleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("是否对您当前的图片进行操作");
        final String[] items = {"是", "否"};// 创建一个存放选项的数组
        final boolean[] checkedItems = {true, false};// 存放选中状态，true为选中
        // ，false为未选中，和setSingleChoiceItems中第二个参数对应
        // 为对话框添加单选列表项
        // 第一个参数存放选项的数组，第二个参数存放默认被选中的项，第三个参数点击事件
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                }
                checkedItems[arg1] = true;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String str = "";
                for (int i = 0; i < checkedItems.length; i++) {
                    if (checkedItems[i]) {
                        str = items[i];
                    }
                }
                Toast.makeText(MainActivity.this, "你选择了" + str, Toast.LENGTH_SHORT).show();
                if (str.equals("是")) {
                    Intent intent1 = new Intent(MainActivity.this, DrawActivity.class);
                    //用Bundle携带数据
                    Bundle bundle = new Bundle();
                    //传递name参数为tinyphp
                    bundle.putString("jpeg", jpegName);
                    intent1.putExtras(bundle);
                    System.out.println("数据发送过去了");
                    startActivity(intent1);
                } else {
                    picture();
                }
            }
        });
        builder.create().show();
    }

    private void showImg(TImage image) {
        saveToSystemAlbum(image.getOriginalPath());
        singleDialog();
        Toast.makeText(MainActivity.this, "已存储", Toast.LENGTH_SHORT).show();
    }

    public static int getGardenId(){
        return MainActivity.gardenId;
    }

    public void  setBuildingId(String buildingId) {
        MainActivity.buildingId = buildingId;
    }
    public static String getBuildingId(){
        return MainActivity.buildingId;
    }

    private void setGardenId(int gardenId) {
        MainActivity.gardenId = gardenId;
    }
}

