package com.example.collectdata_01;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.example.collectdata_01.adapter.DatalistAdapter;
import com.example.interfaceNet.v1;
import com.example.login.login;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ManageFakeGarden extends AppCompatActivity {
    private DatalistAdapter adapter;
    private MaterialButton manageBtn;
    private List<String> list = new ArrayList<String>();
    public static final MediaType JSONDATA
            = MediaType.get("application/json; charset=utf-8");
    static OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_fake_garden);

        RecyclerView listView = findViewById(R.id.list);

        list.add("数据一");

        adapter = new DatalistAdapter(this, R.layout.datalist_item, list);
        listView.setLayoutManager(new LinearLayoutManager(ManageFakeGarden.this));
        adapter.setmOnItemClickListener(new DatalistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (adapter.getResultMap().keySet().contains(list.get(position))) {
//                    alert_edit(position, list);
                    Toast.makeText(ManageFakeGarden.this, "点击" + list.get(position), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(int position){
                if (adapter.getResultMap().keySet().contains(list.get(position))) {
                    Toast.makeText(ManageFakeGarden.this, "长按" + list.get(position), Toast.LENGTH_SHORT).show();
                }
            }

        });
        listView.setAdapter(adapter);
        
        getFakeGardenLists();


    }

    public void getFakeGardenLists(){
        Map map = new HashMap(1);
        map.put("token", login.token);
        map.put("gardenName", "1");
        String param = JSON.toJSONString(map);

//        post(v1.getFakeGardenListApi, param);
        post(v1.searchGardenApi, param);
    }

    public void post(final String url, final String json) {
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

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        try {
                            updateUI(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    public void updateUI(String lists) {
        Map map = JSON.parseObject(lists, HashMap.class);


    }

    // 弹出dialog修改小区所在社区
    public void alert_edit(final int pos, final List<String> list) {
        final EditText et = new EditText(this);
        et.setText(list.get(pos));
        new AlertDialog.Builder(this).setTitle("修改文件名称")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newName = et.getText().toString();
                        String oldName = list.get(pos);
                        if (newName.equals(oldName))
                            return;
                        boolean b;
                        b = adapter.getResultMap().get(oldName);
                        adapter.getResultMap().remove(oldName);
                        list.remove(pos);
                        list.add(pos, newName);
                        adapter.getResultMap().put(list.get(pos), b);
                        adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("取消", null).show();
    }
}
