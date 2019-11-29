package com.example.interfaceNet;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class v1 {
    private static String TAG = "Interface";
    private static String token;
    static final private String preFix = "http://rap2api.taobao.org/app/mock/234350/api/v1/";
    static final private String user = preFix + "user/";
    static final private String loginApi = user + "login";
    public static final MediaType JSONDATA
            = MediaType.get("application/json; charset=utf-8");


    static OkHttpClient client = new OkHttpClient();

    public void post(final String url, final String json) throws IOException {
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
                    Log.i(TAG, "onFailure: ");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    loginCallback(response.body().string());
                }
            });
            }
        }).start();
    }

    public void login(String iemi, String password) throws IOException {
        Map map = new HashMap<String, String>(5);
        map.put("iemi", iemi);
        map.put("password", password);
        post(loginApi, JSON.toJSONString(map));
    }

    private void loginCallback(String res){
        Map map = JSON.parseObject(res, HashMap.class);
//        Map data = map.get("data");
        if(map.get("code").toString().equals("0")){
            String data = map.get("data").toString();
            String[] keyValue = data.substring(1, data.length() - 1).split(":");
            v1.token = keyValue[1];
            loginSucess();
        }
        else {

        }
    }
    // TODO 此处需要处理回调，进行页面切换
    public void loginSucess(){ }

}
