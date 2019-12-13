package com.example.BuildingMessage;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectdata.BuweiActivity;
import com.example.collectdata.bean.CommonItemBean;
import com.example.collectdata_01.R;
import com.example.dialog.CreatDialog;
import com.example.login.login;
import com.example.test.BaseActivity;
import com.example.test.RequestListener;
import com.example.test.RequestTools;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Response;

import static com.google.android.material.tabs.TabLayout.MODE_SCROLLABLE;

public class BuildingActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private Button submit, buwei;
    private TabLayout tabLayout;
    private MyAdapter adapter2;

    private Dialog buildingNumDialog;
    private View BuildNumView;
    private Button add;
    private Button del;
    //    private Button modify;
    private Map<String, String> map2 = new HashMap<>();
    private String mode, locationDescription = "";
    // resultList用来保存不同tab中填写的数据
    private HashMap<Integer, HashMap<String, String>> resultList = new HashMap();

    private List<CommonItemBean> list = new ArrayList<>();
    private HashMap<Integer, List<CommonItemBean>> tabMap = new HashMap<>();
    private HashMap<Integer, List<CommonItemBean>> newtabMap = new HashMap<>();
    private MyHandler handler = new MyHandler(this);
    private final static int REQUEST_CODE = 1; // 返回的结果码

    @Override
    protected int initLayout() {
        return R.layout.buildingactivity;
    }

    @Override
    protected void initData() {
        final Intent i = getIntent();
        mode = i.getStringExtra("mode");
        int gardenId = i.getIntExtra("gardenId", 0);
        String url = i.getStringExtra("url");
        HashMap<String, Object> map = new HashMap<>();
        map.put("token", login.token);
        map.put("gardenId", gardenId);
        new RequestTools(url, map, new RequestListener() {
            @Override
            public void onSuccess(Response response) {
                try {
                    JTools.jsonParasForMessageList(Objects.requireNonNull(response.body()).string(), tabMap, mode);
                } catch (Exception e) {
                    Log.i("json", "json解析错误");
                }
                // 发送message提醒主线程更新ui，注意此段代码是在子线程中运行
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(String info) {

            }
        }).run();
    }

    @Override
    protected void initView() {
        submit = findViewById(R.id.building_submit_test);
        buwei = findViewById(R.id.buwei);
        recyclerView = findViewById(R.id.list);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabMode(MODE_SCROLLABLE);
        tabLayout.addOnTabSelectedListener(tabSelectListener);

        adapter2 = new MyAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter2);
        if (mode.equals("base")) {
            buwei.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(BuildingActivity.this, BuweiActivity.class);
                    i.putExtra("locationDescription", locationDescription);
                    startActivityForResult(i, REQUEST_CODE);
                }
            });
        } else
            buwei.setVisibility(View.GONE);

//        if (mode.equals("base")){
//            int pos = tabLayout.getSelectedTabPosition();
//            TabLayout.Tab selectTab = tabLayout.getTabAt(pos);
//            try {
//                Class c = selectTab.getClass();
//                Field field = c.getDeclaredField("view");
//                field.setAccessible(true);
//                final View view = (View) field.get(selectTab);
//                if (view == null) return;
//                int i = (int) view.getTag();
//                submit.setOnClickListener(new PostListener(this, adapter2.getResultMap(), tabMap.get(i), mode, requestListener));
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }else {
//            submit.setOnClickListener(new PostListener(this, adapter2.getResultMap(), list, mode, requestListener));
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                locationDescription = data.getExtras().getString("locationDescription");
                map2.put("locationDescription", locationDescription);
                System.out.println("getExtra=" + locationDescription);
            }
        }
    }

    private void initDialog() {
        BuildNumView = getLayoutInflater().inflate(R.layout.buildingtab, null);
        buildingNumDialog = CreatDialog.createChangeMarkDialog(BuildingActivity.this, BuildNumView);
        add = BuildNumView.findViewById(R.id.add_tab);
        del = BuildNumView.findViewById(R.id.del_tab);
//        modify = BuildNumView.findViewById(R.id.modify_text);
    }

    private void updateUI() {
        adapter2.setData(tabMap.get(2));

        // 此段代码之前是在initView()中，后因为根据从服务器请求的数据建立tab，故放在此处
        for (int i : tabMap.keySet()) {
            final TabLayout.Tab tab = tabLayout.newTab().setText("楼栋" + i);
            addTabListener(tabLayout, tab, i);
            if (tabLayout.getSelectedTabPosition() == -1) {
                tabLayout.selectTab(tab);
                adapter2.setData(tabMap.get(i));
            }
            tabLayout.addTab(tab);
        }

        int pos = tabLayout.getSelectedTabPosition();
        TabLayout.Tab selectTab = tabLayout.getTabAt(pos);
        try {
            assert selectTab != null;
            Class c = selectTab.getClass();
            Field field = c.getDeclaredField("view");
            field.setAccessible(true);
            final View view = (View) field.get(selectTab);
            if (view == null) return;
            int i = (int) view.getTag();
            submit.setOnClickListener(new PostListener(this, adapter2.getResultMap(), tabMap.get(i), mode, i, map2, requestListener));
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter2.notifyDataSetChanged();
    }

    private void showToast() {
        Toast.makeText(BuildingActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    // 给tab添加OnLongClickListener，使用反射获取到tab的类，将对应楼栋的id设置为view的tag
    private void addTabListener(final TabLayout tabLayout, final TabLayout.Tab tab, final int i) {
        Class c = tab.getClass();
        try {
            Field field = c.getDeclaredField("view");
            field.setAccessible(true);
            final View view = (View) field.get(tab);
            if (view == null) return;
            view.setTag(i);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    initDialog();
                    buildingNumDialog.show();
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; ; i++) {
                                if (!tabMap.keySet().contains(i)) {
                                    TabLayout.Tab tab1 = tabLayout.newTab().setText("新楼栋");
                                    newtabMap.put(i, tabMap.get((int) view.getTag()));
                                    try {

                                        Class c = tab1.getClass();
                                        Field field = c.getDeclaredField("view");
                                        field.setAccessible(true);
                                        final View view = (View) field.get(tab1);
                                        view.setTag(i);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
//                                    addTabListener(tabLayout, tab1, i);
                                    tabLayout.addTab(tab1);
                                    buildingNumDialog.dismiss();
                                    break;
                                }
                            }
                        }
                    });
                    del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 使用tabLayout.removeTabAt((int) view.getTag());会删除错误的位置
                            tabLayout.removeTab(tab);
//                            tabLayout.removeTabAt((int) view.getTag());
                            if (tabMap.keySet().contains(view.getTag()))
                                tabMap.remove((int) view.getTag());
                            resultList.remove((int) view.getTag());
                            buildingNumDialog.dismiss();
                        }
                    });
//                    modify.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            alert_edit(view, tab);
//                            buildingNumDialog.dismiss();
//                        }
//                    });
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class MyHandler extends Handler {
        WeakReference<AppCompatActivity> mActivity;

        MyHandler(AppCompatActivity activity) {
            mActivity = new WeakReference<AppCompatActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ((BuildingActivity) mActivity.get()).updateUI();
                    break;
                case 1:
                    ((BuildingActivity) mActivity.get()).showToast();
            }
        }
    }

    // 深复制hashMap
    public static HashMap<String, String> copy(HashMap<String, String> original) {
        HashMap<String, String> copy = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : original.entrySet()) {
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy;
    }

    private RequestListener requestListener = new RequestListener() {
        @Override
        public void onSuccess(Response response) {
            int code = -1;
            try {
                String result = response.body().string();
                Log.i("http2", "onResponse: " + result);
                JSONObject jsonObject = new JSONObject(result);
                code = jsonObject.getInt("code");
            } catch (Exception e) {
                Log.i("json", "json解析错误");
            }
            if (code == 0) {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }

        @Override
        public void onFailure(String info) {

        }
    };

    private TabLayout.OnTabSelectedListener tabSelectListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {

            Class c = tab.getClass();
            try {
                Field field = c.getDeclaredField("view");
                field.setAccessible(true);
                final View view = (View) field.get(tab);
                if (view == null) return;
                // Toast.makeText(BuildingActivity.this,"第" +(int) view.getTag() + "个tab selected",Toast.LENGTH_SHORT).show();
                int i = (int) view.getTag();
                HashMap<String, String> map = resultList.get(i);
                if (map != null)
                    adapter2.setResultMap(copy(map));
                if (tabMap.keySet().contains(i)) {
                    adapter2.setData(tabMap.get(i));
                    submit.setOnClickListener(new PostListener(BuildingActivity.this, adapter2.getResultMap(), tabMap.get(i), mode, i, map2, requestListener));
                } else {
                    adapter2.setData(newtabMap.get(i));
                    submit.setOnClickListener(new PostListener(BuildingActivity.this, adapter2.getResultMap(), newtabMap.get(i), mode, -1, map2, requestListener));
                }
                adapter2.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            // 切换tab时，先保存当前tab的数据，存放在resultList，再根据新tab的tag从resultList中获取新的数据，设置给adapter
            Class c = tab.getClass();
            try {
                Field field = c.getDeclaredField("view");
                field.setAccessible(true);
                final View view = (View) field.get(tab);
                if (view == null) return;
                // Toast.makeText(BuildingActivity.this,"第" +(int) view.getTag() + "个tab Unselected",Toast.LENGTH_SHORT).show();
                int i = (int) view.getTag();
                // adapter2.getResultMap()获得的是一个hashMap，需要深复制
                resultList.put(i, copy(adapter2.getResultMap()));
                adapter2.clearResultMap();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

//    public void alert_edit(final View view, final TabLayout.Tab tab) {
//        final EditText et = new EditText(this);
//        new AlertDialog.Builder(this).setTitle("请输入楼栋id")
//                .setView(et)
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        //按下确定键后的事件
//                        try {
//                            String s = et.getText().toString();
//                            int id = Integer.parseInt(s);
//                            if (tabMap.keySet().contains(id)) {
//                                Toast.makeText(BuildingActivity.this, "该ID已存在", Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                            tab.setText("楼栋" + id);
//                            tabMap.put(id, tabMap.get((int) view.getTag()));
//                            tabMap.remove((int) view.getTag());
//                            resultList.put(id, resultList.get((int) view.getTag()));
//                            resultList.remove((int) view.getTag());
//                        } catch (Exception e) {
//                            Toast.makeText(BuildingActivity.this, "请输入数字类型", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }).setNegativeButton("取消", null).show();
//    }

}
