package com.example.interfaceNet;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.collectdata_01.MainActivity;
import com.example.collectdata_01.R;
import com.example.login.login;

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

    Context context;
    private static String TAG = "Interface";
    private static String token;
    private static String preFix;

    static final private String user = preFix + "user/";
    static final private String loginApi = user + "login";
    public static final MediaType JSONDATA
            = MediaType.get("application/json; charset=utf-8");
    static OkHttpClient client = new OkHttpClient();

    public v1(Context context) {
        this.context = context;
        Resources res = context.getResources();
        this.setPreFix(res.getString(R.string.preFix));
    }

    public void setPreFix(String preFix){
        v1.preFix = preFix;
    }

    public String getPreFix() {
        return  v1.preFix;
    }

    public void setToken(String token){
        v1.token = token;
    }

    public String getToken() {
        return  v1.token;
    }

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
//                    loginCallback(response.body().string());
                }
            });
            }
        }).start();
    }
}
