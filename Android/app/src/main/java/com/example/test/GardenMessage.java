package com.example.test;

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

import com.example.collectdata.NearByActivity;
import com.example.collectdata.bean.CommonItemBean;
import com.example.collectdata.bean.SelectorItemBean;
import com.example.collectdata_01.R;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Response;

public class GardenMessage extends BaseActivity {

    private MyAdapter adapter2;
    private List<CommonItemBean> list = new ArrayList<>();
    private ArrayList<Integer> radius = new ArrayList<>();
    private Handler handler = new MyHandler(this);
    private String mode;
    private final static int REQUEST_CODE = 1; // 返回的结果码
    private Map<String, Object> map2 = new HashMap<>();

    @Override
    protected int initLayout() {
        return R.layout.activity_test;
    }

    protected void initData() {
        final Intent i = getIntent();
        int gardenId = i.getIntExtra("gardenId",0);
        String url = i.getStringExtra("url");
        mode = i.getStringExtra("mode");
        new RequestTools(url, gardenId, new RequestListener() {
            @Override
            public void onSuccess(Response response) {

                try {
                    JTools.jsonParasForMessageList(Objects.requireNonNull(response.body()).string(), list,radius,mode);
                }catch (Exception e){
                     Log.i("json","json解析错误");
                }
                // 发送message提醒主线程更新ui，注意此段代码是在子线程中运行
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(String info) {
                Toast.makeText(GardenMessage.this,info, Toast.LENGTH_SHORT).show();
            }
        }).run();
    }

    protected void initView() {
        RecyclerView recyclerView = findViewById(R.id.list);
        Button button = findViewById(R.id.message_submit_test);
        Button button2 = findViewById(R.id.message_other);
        adapter2 = new MyAdapter(this,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter2);
        map2 = new HashMap<String, Object>() ;
        button.setOnClickListener(new PostListener(GardenMessage.this,adapter2.getResultMap(),list,mode,map2,new RequestListener() {
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
                        Toast.makeText(GardenMessage.this, info, Toast.LENGTH_SHORT).show();
                    }
                }));
        if(mode.equals("base")){
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(GardenMessage.this, NearByActivity.class);
                    i.putIntegerArrayListExtra("radius", radius);
                    startActivityForResult(i,REQUEST_CODE);
                }
            });
        }
        else
            button2.setVisibility(View.GONE);
    }

    private void updateUI() {
        adapter2.setData(list);
        for (CommonItemBean commonItemBean : list){
            if (commonItemBean instanceof SelectorItemBean){
                if (commonItemBean.getType() == 1) {
                    SelectorItemBean bean = (SelectorItemBean) commonItemBean;
                    adapter2.getResultMap().put(bean.getTitle(), bean.getCurrentSelect());
                }else {
                    SelectorItemBean bean = (SelectorItemBean) commonItemBean;
                    String content = "";
                    for (String s : bean.getCurrentSelects()) {
                        content += s;
                        content += "&";
                    }
                    adapter2.getResultMap().put(bean.getTitle(),content);
                }
            }else {
                adapter2.getResultMap().put(commonItemBean.getTitle(),commonItemBean.getContent());
            }
        }
        adapter2.notifyDataSetChanged();
    }

    private void showToast(){
        Toast.makeText(GardenMessage.this,"上传成功", Toast.LENGTH_SHORT).show();
        finish();
    }

    /** 静态内部类防止内存泄漏（感觉问题不大）
        handleMessage方法接收子线程发来的消息
     */
    static class MyHandler extends Handler {
        WeakReference<AppCompatActivity> mActivity;

        MyHandler(AppCompatActivity activity) {
            mActivity = new WeakReference<AppCompatActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ((GardenMessage) mActivity.get()).updateUI();
                    break;
                case 1:
                    ((GardenMessage) mActivity.get()).showToast();
            }
        }
    }

//     @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == REQUEST_CODE) {
//                map2 = (Map<String, Object>) data.getExtras().getSerializable("map");
//            }
//        }
//    }

}
