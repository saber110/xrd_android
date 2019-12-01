package com.example.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.example.collectdata.bean.CommonItemBean;
import com.example.collectdata.bean.MessageListBean;
import com.example.collectdata.tools.JsonTools;
import com.example.collectdata_01.MainActivity;
import com.example.collectdata_01.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class login extends AppCompatActivity {


    private EditText etUsername;
    private EditText etPassword;
    private Button btGo;
    private FloatingActionButton fab;
    private static String TAG = "Interface";
    public static String token;
//    TODO 使用res中的string字符串
    final private String user = "http://kms.yinaoxiong.cn:8888/api/v1/" + "user/";//http://192.168.43.84:5000/api/v1/get_data/
    final private String loginApi = user + "login";
    public static final MediaType JSONDATA
            = MediaType.get("application/json; charset=utf-8");

    static OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_one);
        checkPermissions(needPermissions);
        initView();

        setListener();
    }

    private void initView() {
        etUsername = findViewById(R.id.et_username);
//        etUsername.setText(getIemi.getIMEI(this));
        etUsername.setText("1");

        etUsername.setFocusable(false);
        etUsername.setFocusableInTouchMode(false);
        etPassword = findViewById(R.id.et_password);
        etPassword.setText("1");
        btGo = findViewById(R.id.bt_go);
        fab = findViewById(R.id.fab);
    }

    public void login(String iemi, String password) throws IOException {
        Map map = new HashMap<String, String>(5);
        map.put("iemi", iemi);
        map.put("password", password);
        post(loginApi, JSON.toJSONString(map));
    }

    private void setListener() {
        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    login(etUsername.getText().toString(), etPassword.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login.this, updatePassword.class));
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fab.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
    }

    private void loginCallback(String res){
        Map map = JSON.parseObject(res, HashMap.class);
//        Map data = map.get("data");
        if(map.get("code").toString().equals("0")){
            String data = map.get("data").toString();

            loginSucess(data);
        }
        else {
            loginFailure(map.get("message").toString());
        }
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
    public void loginSucess(String data){
        String[] keyValue = data.substring(1, data.length() - 1).split(":");
        login.token = keyValue[1].substring(1,keyValue[1].length()-1);
        Log.i(TAG, "loginSucess: " + login.token);
        Intent i2 = new Intent(login.this, MainActivity.class);
        startActivity(i2);
    }

    public void loginFailure(String res){
        Toast.makeText(login.this, res, Toast.LENGTH_SHORT).show();

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
                    loginCallback(response.body().string());
                }
            });
            }
        }).start();
    }

}
