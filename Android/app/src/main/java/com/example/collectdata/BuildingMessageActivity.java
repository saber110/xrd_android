package com.example.collectdata;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collectdata.adapter.MessageListAdapter;
import com.example.collectdata.bean.CommonItemBean;
import com.example.collectdata.listener.SubmitListener;
import com.example.collectdata.tools.CacheTools;
import com.example.collectdata_01.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

//此类和MessageActivity相同，之后应该要修改启动方法，在v1.java的回调函数里
public class BuildingMessageActivity extends AppCompatActivity {
    private ListView listView;
    private Button button;
    private TabLayout tabLayout;

    public static ArrayList<List<CommonItemBean>> buildingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);
        CacheTools.listViewMessage.clear();
        listView = findViewById(R.id.building_message_list);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //TODO 从数据库中请求到的楼栋信息储存在一个二维数组中，将其中一份数据显示，并添加tab
//        tabLayout.addTab(new TabLayout.Tab().setText("1栋"));
        MessageListAdapter mla = new MessageListAdapter(this, CacheTools.pageType);

        listView.setAdapter(mla);

        button = (Button)this.findViewById(R.id.building_message_submit);
        button.setOnClickListener(new SubmitListener(this,mla.getViewItemBeanMap()));

    }
}
