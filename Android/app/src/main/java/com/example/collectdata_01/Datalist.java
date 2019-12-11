package com.example.collectdata_01;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collectdata_01.util.UploadImgUtil;
import com.example.login.login;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import static com.example.collectdata_01.MainActivity.mainDB;

public class Datalist extends AppCompatActivity {
    ArrayList<Users> gardenlist = new ArrayList<>();
    ArrayList<Users> buildinglist = new ArrayList<>();
    ArrayList<Users> qitalist = new ArrayList<>();
    private MaterialButton uploadPictures;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datalist);
        final TextView textView = (TextView)findViewById(R.id.text);
        uploadPictures = findViewById(R.id.uploadPictures);
        ListView listView=(ListView)findViewById(R.id.list);
        List<String> list = new ArrayList<String>();
        //要添加的内容直接添加到list 队列里面就可显示出来   如
//        list.add("数据一");

        dividedData(list);

        ///可以一直添加，在真机运行后可以下拉列表
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);

        uploadPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPictures();
            }
        });
    }

    private void uploadPictures() {
        UploadImgUtil uploadImgUtil = new UploadImgUtil(Datalist.this);
        for (int i = 0; i < gardenlist.size(); i++) {
            uploadImgUtil.uploadGardenImg(gardenlist.get(i).getGardenId(), gardenlist.get(i).getpictureKind(), gardenlist.get(i).getCollectTime(), login.token, gardenlist.get(i).getImage());
            mainDB.delete(gardenlist.get(i));
        }
        for (int i = 0; i < buildinglist.size(); i++) {
            // 复用gardenId
            uploadImgUtil.uploadBuildImg(buildinglist.get(i).getBuildingName(), buildinglist.get(i).getCollectTime(), Integer.toString(MainActivity.getGardenId()), buildinglist.get(i).getpictureKind(), buildinglist.get(i).getImage());
            mainDB.delete(buildinglist.get(i));
        }
        for (int i = 0; i < qitalist.size(); i++) {
            uploadImgUtil.uploadOtherImg(qitalist.get(i).getGardenId(), qitalist.get(i).getCollectTime(), login.token, qitalist.get(i).getImage());
            mainDB.delete(qitalist.get(i));
        }
    }
    private void dividedData(List<String> listView) {
        gardenlist.clear();
        buildinglist.clear();
        qitalist.clear();
        ArrayList<Users> list = mainDB.query(Users.class);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getpictureKind().equals("平面图")
                    || list.get(i).getpictureKind().equals("小区入口")
                    || list.get(i).getpictureKind().equals("外景图")
                    || list.get(i).getpictureKind().equals("内景图")) {
                gardenlist.add(list.get(i));
            }
            if (list.get(i).getpictureKind().equals("建筑立面")
                    || list.get(i).getpictureKind().equals("幢牌号")) {
                buildinglist.add(list.get(i));
            }
            if (list.get(i).getpictureKind().equals("其他")) {
                qitalist.add(list.get(i));
            }
            listView.add(list.get(i).getImage());
        }
    }

}

