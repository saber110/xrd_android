package com.example.collectdata_01.util;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.collectdata_01.R;
import com.example.database.ImageDb;
import com.example.interfaceNet.v1;
import com.example.login.login;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import static com.example.collectdata_01.Datalist.adapter;
import static com.example.collectdata_01.MainActivity.mainDB;
import static com.litesuits.orm.db.impl.CascadeSQLiteImpl.TAG;
import static java.lang.String.valueOf;

/**
 * 上传图片的接口工具类
 */
public class UploadImgUtil {

    public static int N;
    public int n = 0;

    public Context context;

    public void funToastMakeText(String msg) {
        Looper.prepare();
        Log.i(TAG, "funToastMakeText: " + msg);
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    public UploadImgUtil(Context context) {
        this.context = context;
    }

    public  Context getContext() {
        return context;
    }



    private void postFile(final String url, final Map<String, String> map, final String jpeg) {
        System.out.println(jpeg);
        adapter.setProcess(jpeg,"上传中");
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==0){
                    adapter.setProcess(jpeg,msg.obj.toString());
                    Toast.makeText(getContext(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        };
        OkHttpClient client = new OkHttpClient();
        final Message msg = new Message();
        msg.what=0;

        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File file = new File(Environment.getExternalStorageDirectory(), "/"+ context.getResources().getString(R.string.picturePath) + "/" + jpeg);
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
        client.newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .build()
                .newCall(request)
                .enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d(">>>>>>", "onFailure: 上传图片失败");
                msg.obj="上传失败";
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String str = response.body().string();

                    setUploadedByCollecttime(map.get(ImageDb.COLLECTTIOME_COL));
                    n++;
                    if(n >= N){
                        msg.obj="上传成功";
                        handler.sendMessage(msg);
                        n = 0;
                    }
                    Log.d(">>>", "onResponse: "+str);
                } else {
                    Log.i(">>>>" ,response.message() + " error : body " + response.body().string());
                    msg.obj="上传失败";
                    handler.sendMessage(msg);
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
        map.put("token", login.token);
        map.put("image", jpeg);
        postFile(v1.uploadGardenPictureApi,map,jpeg);
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
        map.put("token", login.token);
        map.put("image", jpeg);
        postFile(v1.uploadOtherPictureApi, map, jpeg);
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
    public void uploadBuildImg(String buildingName,String collectTime,String gardenId,String pictureKind,String jpeg){
        Map<String,String> map = new HashMap(8);
        map.put("buildingName",buildingName);
        map.put("pictureKind",pictureKind);
        map.put("collectTime",collectTime);
        map.put("token", login.token);
        map.put("gardenId",gardenId);
        map.put("image", jpeg);
        postFile(v1.uploadBuildingPictureApi,map, jpeg);
    }

    public void setUploadedByCollecttime(String collectTime){
        // 设置数据库中的上传控制项
        // collectTime唯一
        ArrayList<ImageDb> updateUser = mainDB.query(new QueryBuilder<ImageDb>(ImageDb.class)
                .whereEquals(ImageDb.COLLECTTIOME_COL , collectTime));
        updateUser.get(0).setIsuploaded(true);
        ColumnsValue cv = new ColumnsValue(new String[]{ImageDb.ISUPLOADED_COL});
        mainDB.update(updateUser.get(0), cv, ConflictAlgorithm.None);
    }
}
