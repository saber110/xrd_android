package com.example.test;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initLayout());
        initData();
        initView();
    }

    protected abstract int initLayout();

    protected abstract void initView();

    protected abstract void initData();
}
