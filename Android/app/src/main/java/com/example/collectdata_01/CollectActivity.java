package com.example.collectdata_01;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.collectdata_01.adapter.RecyclerAdapter;
import com.example.collectdata_01.model.DataBean;

import java.util.ArrayList;
import java.util.List;

public class CollectActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<DataBean> dataBeanList;
    private DataBean dataBean1;
    private DataBean dataBean2;
    private DataBean dataBean3;
    private DataBean dataBean4;
    private RecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collect);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);

        initData();
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_ba);

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_home_white_24dp, "Home"))
                .addItem(new BottomNavigationItem(R.drawable.ic_book_white_24dp, "Books"))
                .addItem(new BottomNavigationItem(R.drawable.ic_music_note_white_24dp, "Music"))
                .addItem(new BottomNavigationItem(R.drawable.ic_tv_white_24dp, "Movies & TV"))
                .addItem(new BottomNavigationItem(R.drawable.ic_videogame_asset_white_24dp, "Games"))
                .initialise();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
            }
        });
    }


    /**
     * 模拟数据
     */
    private void initData(){
        dataBeanList = new ArrayList<>();
//        for (int i = 1; i <= 50; i++) {
//            dataBean = new DataBean();
//            dataBean.setID(i+"");
//            dataBean.setType(0);
//            dataBean.setParentLeftTxt("父--"+i);
//            dataBean.setParentRightTxt("父内容--"+i);
//            dataBean.setChildLeftTxt("子--"+i);
//            dataBean.setChildRightTxt("子内容--"+i);
//            dataBean.setChildBean(dataBean);
//            dataBeanList.add(dataBean);
//        }
        dataBean1 = new DataBean();
        dataBean1.setID("1");
        dataBean1.setType(dataBean1.PARENT_ITEM);
        dataBean1.setForChildType(dataBean1.CHILD_ITEM1);
        dataBean1.setParentLeftTxt("小区概况表");
        dataBean1.setParentRightTxt("");
//        dataBean1.setChildLeftTxt("");
//        dataBean1.setChildRightTxt("子内容--");
        dataBean1.setChildBean(dataBean1);
        dataBeanList.add(dataBean1);

        dataBean2 = new DataBean();
        dataBean2.setID("2");
        dataBean2.setType(dataBean2.PARENT_ITEM);
        dataBean2.setForChildType(dataBean2.CHILD_ITEM2);
        dataBean2.setParentLeftTxt("楼幢调查");
        dataBean2.setParentRightTxt("");
//        dataBean2.setChildLeftTxt("");
//        dataBean2.setChildRightTxt("子内容二");
        dataBean2.setChildBean(dataBean2);
        dataBeanList.add(dataBean2);

        dataBean3 = new DataBean();
        dataBean3.setID("3");
        dataBean3.setType(dataBean3.PARENT_ITEM);
        dataBean3.setForChildType(dataBean3.CHILD_ITEM3);
        dataBean3.setParentLeftTxt("小区信息");
        dataBean3.setParentRightTxt("");
//        dataBean3.setChildLeftTxt("");
//        dataBean3.setChildRightTxt("子内容三");
        dataBean3.setChildBean(dataBean3);
        dataBeanList.add(dataBean3);

        dataBean4 = new DataBean();
        dataBean4.setID("4");
        dataBean4.setType(dataBean4.PARENT_ITEM);
        dataBean4.setForChildType(dataBean4.CHILD_ITEM4);
        dataBean4.setParentLeftTxt("楼幢信息");
        dataBean4.setParentRightTxt("");
//        dataBean4.setChildLeftTxt("");
//        dataBean4.setChildRightTxt("子内容四");
        dataBean4.setChildBean(dataBean4);
        dataBeanList.add(dataBean4);
        setData();
    }

    private void setData(){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerAdapter(this,dataBeanList);
        mRecyclerView.setAdapter(mAdapter);
        //滚动监听
        mAdapter.setOnScrollListener(new RecyclerAdapter.OnScrollListener() {
            @Override
            public void scrollTo(int pos) {
                mRecyclerView.scrollToPosition(pos);
            }
        });
    }

}
