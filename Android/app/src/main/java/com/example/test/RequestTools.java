package com.example.test;

import com.alibaba.fastjson.JSON;
import com.example.login.login;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.interfaceNet.v1.JSONDATA;

public class RequestTools implements Runnable {
        public String url;
        public RequestListener listener;
        static OkHttpClient client = new OkHttpClient();
        Map map = new HashMap<String, String>(5);

        public RequestTools(String url, int gardenId, RequestListener listener){
            map.put("token", login.token);
//            map.put("buildingId", buildingId);
            map.put("gardenId", gardenId);
            this.url = url;
            this.listener = listener;
        }
        public RequestTools(String url, HashMap map1, RequestListener listener){
            map = map1;
            this.url = url;
            this.listener = listener;
        }

    @Override
    public void run() {
        RequestBody body = RequestBody.create(JSON.toJSONString(map), JSONDATA);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.onFailure(e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                listener.onSuccess(response);
            }
        });
    }
}
