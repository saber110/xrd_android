package com.example.collectdata_01.util;

import android.os.Environment;
import android.util.Log;

import com.example.login.login;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.String.valueOf;

/**
 * 上传图片的接口工具类
 */
public class UploadImgUtil {


    private void postFile(final String url, Map<String, String> map, String jpeg) {
        OkHttpClient client = new OkHttpClient();
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + jpeg);
        if(file != null){
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/jpeg"), file);

            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("image", file.getName(), body);
        }

        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry entry : map.entrySet()) {
                requestBody.addFormDataPart(valueOf(entry.getKey()), valueOf(entry.getValue()));
            }
        }
        Request request = new Request.Builder().url(url).post(requestBody.build()).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(">>>>>>", "onFailure: 上传图片失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String str = response.body().string();
                    Log.d(">>>", "onResponse: "+str);
                } else {
                    Log.i(">>>>" ,response.message() + " error : body " + response.body().string());
                }
            }
        });

    }

    /**
     * 上传小区图片接口
     * {
     *   "gardenId": "1",
     *   "pictureKind": "平面图",
     *   "collectTime": "1573007251381",
     *   "token": "",
     *   "image": ""
     * }
     */
    public void uploadGardenImg(String gardenId,String pictureKind,String collectTime,String token, String jpeg){
        Map<String,String> map = new HashMap(4);
        map.put("gardenId",gardenId);
        map.put("pictureKind",pictureKind);
        map.put("collectTime",collectTime);
        map.put("token", token);
        map.put("image", jpeg);
        postFile("http://rap2api.taobao.org/app/mock/234350/api/v1/data/garden_picture",map,jpeg);
    }

    /**
     * 上传其他接口
     * {
     *   "gardenId": "1",
     *   "collectTime": "1573007251381",
     *   "token": "",
     *   "image": ""
     * }
     */
    public void uploadOtherImg(String gardenId,String collectTime,String token, String jpeg){
        Map<String,String> map = new HashMap(4);
        map.put("gardenId",gardenId);
        map.put("collectTime",collectTime);
        map.put("token", token);
        map.put("image", jpeg);
        postFile("http://rap2api.taobao.org/app/mock/234350/api/v1/data/other_picture",map, jpeg);
    }

    /**
     * 上传楼栋接口
     * {
     *   "buildingId": "1",
     *   "collectTime": "1573007251381",
     *   "token": "",
     *   "image": "",
     *   "gardenId": "",
     *   "pictureKind": ""
     * }
     */
    public void uploadBuildImg(String buildingId,String collectTime,String gardenId,String pictureKind,String jpeg){
        Map<String,String> map = new HashMap(8);
        map.put("buildingId",buildingId);
        map.put("pictureKind",pictureKind);
        map.put("collectTime",collectTime);
        map.put("token", login.token);
        map.put("gardenId",gardenId);
        map.put("image", jpeg);
        postFile("http://rap2api.taobao.org/app/mock/234350/api/v1/data/building_picture",map, jpeg);
    }
}
