package com.example.login;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import com.example.collectdata_01.MainActivity;
import com.example.collectdata_01.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.iemi.getIemi;

public class login extends AppCompatActivity {


    private EditText etUsername;
    private EditText etPassword;
    private Button btGo;
    private FloatingActionButton fab;

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
        etUsername.setText(getIemi.getIMEI(this));
        etUsername.setFocusable(false);
        etUsername.setFocusableInTouchMode(false);
        etPassword = findViewById(R.id.et_password);
        btGo = findViewById(R.id.bt_go);
        fab = findViewById(R.id.fab);
    }

    private void setListener() {
        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Explode explode = new Explode();
                explode.setDuration(500);

                getWindow().setExitTransition(explode);
                getWindow().setEnterTransition(explode);
                ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(login.this);
                Intent i2 = new Intent(login.this, MainActivity.class);
                startActivity(i2, oc2.toBundle());
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(login.this, fab, fab.getTransitionName());
                startActivity(new Intent(login.this, register.class), options.toBundle());
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
}
