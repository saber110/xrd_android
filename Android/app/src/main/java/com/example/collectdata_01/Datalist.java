package com.example.collectdata_01;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import static com.example.collectdata_01.MainActivity.mainDB;

public class Datalist extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datalist);
        final TextView textView = (TextView)findViewById(R.id.text);
        ListView listView=(ListView)findViewById(R.id.list);
        List<String> list = new ArrayList<String>();
        //要添加的内容直接添加到list 队列里面就可显示出来   如
        list.add("数据一");
        // .......
        ///可以一直添加，在真机运行后可以下拉列表
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);
    }
}

