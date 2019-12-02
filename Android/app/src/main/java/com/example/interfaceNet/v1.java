package com.example.interfaceNet;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.collectdata_01.MainActivity;
import com.example.collectdata_01.R;
import com.example.login.login;

import org.jetbrains.annotations.NotNull;

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
    private static String cbUpdateGardenBaseInfo;
    private static String cbLogin;
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
        // Integer.valueOf(str)
        v1.cbUpdateGardenBaseInfo = (res.getString(R.string.cbUpdateGardenBaseInfo));
        v1.cbLogin = (res.getString(R.string.cbLogin));
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
        Log.i(TAG, "callBack: " + res);
        if(Objects.requireNonNull(map.get("code")).toString().equals(SUCCESS)){
            if(entry.equals(cbLogin)){
                callbackLogin(res);
            }else if(entry.equals(cbUpdateGardenBaseInfo)){
                callbackGardenBaseInfo(res);
            }
        }

    }

    public void getGardenBaseInfoAPI(int gardenId) {
        Map map = new HashMap<String, String>(5);
        map.put("token", login.token);
        map.put("gardenId", gardenId);

        post(getGardenBaseInfoAPI, JSON.toJSONString(map), cbUpdateGardenBaseInfo);
    }

    public void callbackGardenBaseInfo(final String res){
        Log.i(TAG, "callbackGardenBaseInfo: " + res);
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
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
