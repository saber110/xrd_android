package com.example.interfaceNet;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.collectdata.MessageActivity;
import com.example.collectdata.bean.MessageListBean;
import com.example.collectdata.tools.JsonTools;
import com.example.collectdata_01.MainActivity;
import com.example.collectdata_01.R;
import com.example.login.login;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class v1{

    final static String SUCCESS = "0";

    private static Context context;
    private static String TAG = "Interface_v1";
    private static String token;
    private static String preFix;
    private static String pattern;
    private static String apiError;
    private static String loginApi;
    private static String updatePasswordApi;
    private static String getGardenBaseInfoAPI;
    private static String getBuildingBaseInfoAPI;
    private static String getGardenImportInfoApi;
    private static String getBuildingImportInfoApi;
    private static String cbUpdateGardenBaseInfo;
    private static String cbUpdateBuildingBaseInfo;
    private static String cbUpdateGardenImportInfo;
    private static String cbUpdateBuildingImportInfo;
    private static String cbLogin;

    public static String getProvinceApi;
    public static String getCityApi;
    public static String getDistrictApi;
    public static String getStreetApi;
    public static String getCommunityApi;
    public static String searchGardenApi;
    public static String createGardenApi;
    public static String getMapDataAPI;
    public static String addMapDataAPI;
    private static Resources res;


    public static final MediaType JSONDATA
            = MediaType.get("application/json; charset=utf-8");
    static OkHttpClient client = new OkHttpClient();

    public v1(Context context) {
        this.context = context;
        res = context.getResources();
        v1.preFix = res.getString(R.string.preFix);
        v1.pattern = res.getString(R.string.preFix);
        v1.apiError = res.getString(R.string.api_error);
        v1.getGardenBaseInfoAPI = String.format(pattern, res.getString(R.string.getDataTab), res.getString(R.string.getGardenBaseInfoAPI));
        v1.getBuildingBaseInfoAPI = String.format(pattern, res.getString(R.string.getDataTab), res.getString(R.string.getBuildBaseInfoAPI));
        // Integer.valueOf(str)
        v1.getGardenImportInfoApi = String.format(pattern, res.getString(R.string.getDataTab), res.getString(R.string.getGardenImportInfoApi));
        v1.getBuildingImportInfoApi = String.format(pattern, res.getString(R.string.getDataTab), res.getString(R.string.getBuildingImportInfoApi));

        v1.cbLogin = (res.getString(R.string.cbLogin));
        v1.cbUpdateGardenBaseInfo = (res.getString(R.string.cbUpdateGardenBaseInfo));
        v1.cbUpdateBuildingBaseInfo = res.getString(R.string.cbUpdateBuildingBaseInfo);
        v1.cbUpdateGardenImportInfo = res.getString(R.string.cbUpdateGardenImportInfo);
        v1.cbUpdateBuildingImportInfo = res.getString(R.string.cbUpdateBuildingImportInfo);
        v1.createGardenApi = String.format(pattern, res.getString(R.string.dataTab), res.getString(R.string.createGardenApi));
        v1.getProvinceApi = String.format(pattern, res.getString(R.string.administrationTab), res.getString(R.string.getProvinceApi));
        v1.getCityApi = String.format(pattern, res.getString(R.string.administrationTab), res.getString(R.string.getCityApi));
        v1.getDistrictApi = String.format(pattern, res.getString(R.string.administrationTab), res.getString(R.string.getDistrictApi));
        v1.getStreetApi = String.format(pattern, res.getString(R.string.administrationTab), res.getString(R.string.getStreetApi));
        v1.getCommunityApi = String.format(pattern, res.getString(R.string.administrationTab), res.getString(R.string.getCommunityApi));
        v1.searchGardenApi = String.format(pattern, res.getString(R.string.administrationTab), res.getString(R.string.searchGardenApi));
        v1.getMapDataAPI = String.format(pattern,res.getString(R.string.getDataTab), res.getString(R.string.getMapDataAPI));
        v1.addMapDataAPI = String.format(pattern,res.getString(R.string.dataTab), res.getString(R.string.addMapDataAPI));
    }

    public void setToken(String token){
        v1.token = token;
    }

    public String getToken() {
        return  v1.token;
    }

    public void post(final String url, final String json, final String callbackParam) {
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
                    funToastMakeText(apiError);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try {
                        callBack(Objects.requireNonNull(response.body()).string(), callbackParam);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            }
        }).start();
    }

    public void callBack(String res, final String entry) {
        Map map = JSON.parseObject(res, HashMap.class);
        Log.i(TAG, "callBack: " + entry + " " +res);
        if(Objects.requireNonNull(map.get("code")).toString().equals(SUCCESS)){
            if(entry.equals(cbLogin)){
                callbackLogin(res);
            }else if(entry.equals(cbUpdateGardenBaseInfo)){
                callbackGardenBaseInfo(res);
            }else if(entry.equals(cbUpdateBuildingBaseInfo)){
                callbackBuildingBaseInfo(res);
            }else if(entry.equals(cbUpdateBuildingImportInfo)){
                callbackBuildingImportInfo(res);
            }else if(entry.equals(cbUpdateGardenImportInfo)){
                callbackGardenImportInfo(res);
            }
        }

    }

    public void getGardenBaseInfoAPI(int gardenId) {
        Map map = new HashMap<String, String>(5);
        map.put("token", login.token);
        map.put("gardenId", gardenId);

        post(getGardenBaseInfoAPI, JSON.toJSONString(map), cbUpdateGardenBaseInfo);
    }

    public void getBuildingBaseInfoAPI(int buildingId) {
        Map map = new HashMap<String, String>(5);
        map.put("token", login.token);
        map.put("buildingId", buildingId);

        post(getBuildingBaseInfoAPI, JSON.toJSONString(map), cbUpdateBuildingBaseInfo);

    }

    public void getGardenImportInfoAPI(int gardenId) {
        Log.i(TAG, "getGardenImportInfoAPI: " + gardenId);
        Map map = new HashMap<String, String>(5);
        map.put("token", login.token);
        map.put("gardenId", gardenId);

        post(getGardenImportInfoApi, JSON.toJSONString(map), cbUpdateGardenImportInfo);

    }

    public void getBuildingImportInfoAPI(int buildingId) {
        Map map = new HashMap<String, String>(5);
        map.put("token", login.token);
        map.put("buildingId", buildingId);

        post(getBuildingImportInfoApi, JSON.toJSONString(map), cbUpdateBuildingImportInfo);

    }


    public void importGardenInfo(int gardenId) {
        Map map = new HashMap<String, String>(5);
        map.put("token", login.token);
        map.put("gardenId", gardenId);

        post(getGardenBaseInfoAPI, JSON.toJSONString(map), cbUpdateGardenBaseInfo);
    }

    public void importBuildinginfo(int gardenId) {
        Map map = new HashMap<String, String>(5);
        map.put("token", login.token);
        map.put("gardenId", gardenId);

        post(getGardenBaseInfoAPI, JSON.toJSONString(map), cbUpdateGardenBaseInfo);
    }


    public void callbackGardenBaseInfo(final String res){
        Log.i(TAG, "callbackGardenBaseInfo: " + res);
        MessageListBean.list.clear();
        try {
            JsonTools.jsonParasForMessageList(res,MessageListBean.list);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Looper.prepare();
        Intent i2 = new Intent(this.getContext(), MessageActivity.class);
        this.getContext().startActivity(i2);
        Looper.loop();
    }

    public void callbackBuildingBaseInfo(final String res){
        Log.i(TAG, "callbackBuildingBaseInfo: " + res);
        MessageListBean.list.clear();
        try {
            JsonTools.jsonParasForMessageList(res,MessageListBean.list);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Looper.prepare();
        Intent i2 = new Intent(this.getContext(), MessageActivity.class);
        this.getContext().startActivity(i2);
        Looper.loop();
    }

    public void callbackGardenImportInfo(final String res){
        Log.i(TAG, "callbackGardenBaseInfo: " + res);
        MessageListBean.list.clear();
        try {
            JsonTools.jsonParasForMessageList(res,MessageListBean.list);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Looper.prepare();
        Intent i2 = new Intent(this.getContext(), MessageActivity.class);
        this.getContext().startActivity(i2);
        Looper.loop();
    }

    public void callbackBuildingImportInfo(final String res){
        Log.i(TAG, "callbackBuildingBaseInfo: " + res);
        MessageListBean.list.clear();
        try {
            JsonTools.jsonParasForMessageList(res,MessageListBean.list);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Looper.prepare();
        Intent i2 = new Intent(this.getContext(), MessageActivity.class);
        this.getContext().startActivity(i2);
        Looper.loop();
    }

    public void loginApi(String iemi, String password) throws IOException {
        loginApi = String.format(pattern, res.getString(R.string.userTab), res.getString(R.string.loginApi));

        Map map = new HashMap<String, String>(5);
        map.put("iemi", iemi);
        map.put("password", password);
        post(loginApi, JSON.toJSONString(map),cbLogin);
    }

    private void callbackLogin(String res){
        Map map = JSON.parseObject(res, HashMap.class);
        if(map.get("code").toString().equals("0")){
            String data = map.get("data").toString();
            loginSucess(data);
        }
        else {
            loginFailure(map.get("message").toString());
        }
    }

    public void loginSucess(String data){
        String[] keyValue = data.substring(1, data.length() - 1).split(":");
        login.token = keyValue[1].substring(1,keyValue[1].length()-1);
        Log.i(TAG, "loginSucess: " + login.token);
        Looper.prepare();
        Intent i2 = new Intent(this.getContext(), MainActivity.class);
        ((AppCompatActivity)(this.getContext())).finish();
        this.getContext().startActivity(i2);
        Looper.loop();
    }

    public void loginFailure(String res){
        funToastMakeText(res);
    }

    public  Context getContext() {
        return context;
    }

    public void funToastMakeText(String msg) {
        Looper.prepare();
        Log.i(TAG, "funToastMakeText: " + msg);
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
