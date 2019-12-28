package com.example.collectdata_01;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.example.collectdata_01.adapter.FakeGardenListAdapter;
import com.example.collectdata_01.adapter.GetCityViewAdapter;
import com.example.collectdata_01.adapter.GetCommunityViewAdapter;
import com.example.collectdata_01.adapter.GetDistrictViewAdapter;
import com.example.collectdata_01.adapter.GetProvinceViewAdapter;
import com.example.collectdata_01.adapter.GetStreetViewAdapter;
import com.example.collectdata_01.adapter.onItemClickListener;
import com.example.dialog.CreatDialog;
import com.example.interfaceNet.v1;
import com.example.login.login;
import com.example.map.dao.CityDao;
import com.example.map.dao.CommunityDao;
import com.example.map.dao.DistrictDao;
import com.example.map.dao.LocationAllDao;
import com.example.map.dao.ProvinceDao;
import com.example.map.dao.StreetDao;
import com.example.map.net.GetLocationNetUtil;
import com.example.net.AsyncRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ManageFakeGarden extends AppCompatActivity {
    private FakeGardenListAdapter adapter;
    public static final MediaType JSONDATA
            = MediaType.get("application/json; charset=utf-8");
    static OkHttpClient client = new OkHttpClient();

    private RecyclerView selectLocationRecycleView;
    private View selectLocationView;
    private Dialog locationDialog;

    private FakeGardenBean fakeGardenBean;
    private LocationAllDao allDao = new LocationAllDao();
    private List<FakeGardenBean.DataBean.GardenListBean> listBeans = new ArrayList<>();
    private MyHandler handler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_fake_garden);

        RecyclerView listView = findViewById(R.id.list);
        selectLocationView = getLayoutInflater().inflate(R.layout.select_city_dialog, null);
        selectLocationRecycleView = selectLocationView.findViewById(R.id.select_city_recycleview);

        selectLocationRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        locationDialog = CreatDialog.createLocationSelectDialog(ManageFakeGarden.this, selectLocationView);
        locationDialog.setCanceledOnTouchOutside(false);
        adapter = new FakeGardenListAdapter(this, R.layout.garden_list_item, listBeans);
        adapter.setmOnItemClickListener(new onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showProvinceDialog(adapter.getDatalist().get(position).getGardenName(), adapter.getDatalist().get(position).getGardenId());
            }

            @Override
            public void onItemLongClick(int position) {

            }

        });
        listView.setLayoutManager(new LinearLayoutManager(ManageFakeGarden.this));
        listView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        listView.setAdapter(adapter);
        getFakeGardenLists();
    }


    public void getFakeGardenLists() {
        Map map = new HashMap(1);
        map.put("token", login.token);
        String param = JSON.toJSONString(map);

        post(v1.getFakeGardenListApi, param);
//        post("http://kms.yinaoxiong.cn:8888/api/v1/get_data/virtual_garden", param);
    }

    public void post(final String url, final String json) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody body = RequestBody.create(json, JSONDATA);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        try {
                            String s = response.body().string();
                            fakeGardenBean = FakeGardenBean.objectFromData(s);
//                            fakeGardenBean = FakeGardenBean.objectFromData(string);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Message message = new Message();
                        message.what = 18;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }

    public void updateUI() {
        if (fakeGardenBean.getCode() != 0) {
            Toast.makeText(ManageFakeGarden.this, "请求数据出错", Toast.LENGTH_SHORT).show();
            return;
        }
        adapter.setDataList(fakeGardenBean.getData().getGardenList());
        adapter.notifyDataSetChanged();
    }

    public void showToast(String info){
        Toast.makeText(ManageFakeGarden.this,info,Toast.LENGTH_SHORT).show();
    }

    static class MyHandler extends Handler {
        WeakReference<AppCompatActivity> mActivity;

        MyHandler(AppCompatActivity activity) {
            mActivity = new WeakReference<AppCompatActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 18:
                    ((ManageFakeGarden) mActivity.get()).updateUI();
                    break;
                case 19:
                    String info = (String) msg.obj;
                    ((ManageFakeGarden) mActivity.get()).showToast(info);
                    break;
            }
        }
    }

    private void showProvinceDialog(final String s, final int gardenId) {

        GetLocationNetUtil.GetProvinceNetUtil getProvinceNetUtil = new GetLocationNetUtil.GetProvinceNetUtil();
        AsyncTask asyncTask = new AsyncRequest().execute(getProvinceNetUtil);
        try {
            final ProvinceDao provinceDao = (ProvinceDao) asyncTask.get();
            GetProvinceViewAdapter adapter = new GetProvinceViewAdapter(ManageFakeGarden.this,
                    provinceDao.getData());
            adapter.setItemClickListener(new GetProvinceViewAdapter.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    allDao.setProvinceId(provinceDao.getData().getProvinces().get(position).getId());
                    showCityDialog(s, gardenId);
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

    private void showCityDialog(final String s, final int gardenId) {
        GetLocationNetUtil.GetCityNetUtil getCityNetUtil = new GetLocationNetUtil.GetCityNetUtil(allDao.getProvinceId());
        AsyncTask asyncTask = new AsyncRequest().execute(getCityNetUtil);
        try {
            final CityDao cityDao = (CityDao) asyncTask.get();
//            cityDao.getData();
            GetCityViewAdapter adapter = new GetCityViewAdapter(ManageFakeGarden.this,
                    cityDao.getData());
            adapter.setItemClickListener(new GetCityViewAdapter.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    allDao.setCityId(cityDao.getData().getCities().get(position).getId());
                    showDistrict(s, gardenId);
                }
            });
            selectLocationRecycleView.setAdapter(adapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void showDistrict(final String s, final int gardenId) {
        GetLocationNetUtil.GetDistrictNetUtil getDistrictNetUtil = new GetLocationNetUtil.GetDistrictNetUtil(allDao.getCityId());
        AsyncTask asyncTask = new AsyncRequest().execute(getDistrictNetUtil);
        try {
            final DistrictDao districtDao = (DistrictDao) asyncTask.get();
            GetDistrictViewAdapter adapter = new GetDistrictViewAdapter(ManageFakeGarden.this,
                    districtDao.getData());
            adapter.setItemClickListener(new GetDistrictViewAdapter.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    allDao.setDistrictId(districtDao.getData().getDistricts().get(position).getId());
                    showStreet(s, gardenId);
                }
            });
            selectLocationRecycleView.setAdapter(adapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void showStreet(final String s, final int gardenId) {
        GetLocationNetUtil.GetStreetNetUtil getStreetNetUtil = new GetLocationNetUtil.GetStreetNetUtil(allDao.getDistrictId());
        AsyncTask asyncTask = new AsyncRequest().execute(getStreetNetUtil);
        try {
            final StreetDao streetDao = (StreetDao) asyncTask.get();
            GetStreetViewAdapter adapter = new GetStreetViewAdapter(ManageFakeGarden.this,
                    streetDao.getData());
            adapter.setItemClickListener(new GetStreetViewAdapter.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    allDao.setStreetId(streetDao.getData().getStreets().get(position).getId());
                    showCommunity(s, gardenId);
                }
            });
            selectLocationRecycleView.setAdapter(adapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void showCommunity(final String s, final int gardenId) {
        GetLocationNetUtil.GetCommunityNetUtil getCommunityNetUtil = new GetLocationNetUtil.GetCommunityNetUtil(allDao.getStreetId());
        AsyncTask asyncTask = new AsyncRequest().execute(getCommunityNetUtil);
        try {
            final CommunityDao communityDao = (CommunityDao) asyncTask.get();
            GetCommunityViewAdapter adapter = new GetCommunityViewAdapter(ManageFakeGarden.this, communityDao.getData());
            adapter.setItemClickListener(new GetCommunityViewAdapter.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Log.d(">>>>", "onItemClick: " + position);
                    allDao.setCommunityId(communityDao.getData().getCommunities().get(position).getId());
                    modifyGarden(s, gardenId);
                }
            });
            selectLocationRecycleView.setAdapter(adapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void modifyGarden(String gardenName, int gardenId) {
        final Map map = new HashMap(10);
        map.put("token", login.token);
        map.put("provinceId", allDao.getProvinceId());
        map.put("cityId", allDao.getCityId());
        map.put("districtId", allDao.getDistrictId());
        map.put("streetId", allDao.getStreetId());
        map.put("communityId", allDao.getCommunityId());
        map.put("gardenName", gardenName);
        map.put("gardenId", gardenId);
        locationDialog.dismiss();
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody body = RequestBody.create(JSON.toJSONString(map), JSONDATA);
                Request request = new Request.Builder()
                        .url(v1.createGardenApi)
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        String string = "";
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            string = jsonObject.getString("message");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Message message = new Message();
                        message.what = 19;
                        message.obj = string;
                        handler.sendMessage(message);
                    }
                });
            }
        }).start();
    }
}
