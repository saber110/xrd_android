package com.example.collectdata;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collectdata.listener.DataListener;
import com.example.collectdata.tools.ConstTools;
import com.example.collectdata_01.R;

/**
 * 数据采集页面
 */
public class DataActivity extends AppCompatActivity {

    private LinearLayout xiaoqugaikuang;
    private LinearLayout louzhuangdiaocha;
    private LinearLayout xiaoquxinxi;
    private LinearLayout louzhuanxinxi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        //获取组件
        xiaoqugaikuang = this.findViewById(R.id.data_xiaoqugaikuang);
        xiaoquxinxi = this.findViewById(R.id.data_xiaoquxinxi);
        louzhuangdiaocha = this.findViewById(R.id.data_louzhuangdiaocha);
        louzhuanxinxi = this.findViewById(R.id.data_louzhuangxinxi);

        //添加监听器
        xiaoqugaikuang.setOnClickListener(new DataListener(this, ConstTools.XIAOQUGAIKUANG));
        xiaoquxinxi.setOnClickListener(new DataListener(this, ConstTools.XIAOQUXINXI));
        louzhuanxinxi.setOnClickListener(new DataListener(this, ConstTools.LOUZHUANGXINXI));
        louzhuangdiaocha.setOnClickListener(new DataListener(this, ConstTools.LOUZHUANGDIAOCHA));

    }
}
