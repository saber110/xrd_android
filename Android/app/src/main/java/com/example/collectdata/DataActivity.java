package com.example.collectdata;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collectdata.listener.DataListener;
import com.example.collectdata.tools.ConstTools;
import com.example.collectdata_01.R;
import com.example.dialog.CreatDialog;

/**
 * 数据采集页面
 */
public class DataActivity extends AppCompatActivity {

    private LinearLayout xiaoqugaikuang;
    private LinearLayout louzhuangdiaocha;
    private LinearLayout xiaoquxinxi;
    private LinearLayout louzhuanxinxi;

    private Dialog buildingNumDialog;
    private View BuildNumView;
    private Button buttonSubmit;
    private Button buttonCancel;
    private EditText editBuildingNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        //获取组件
        xiaoqugaikuang = this.findViewById(R.id.data_xiaoqugaikuang);
        xiaoquxinxi = this.findViewById(R.id.data_xiaoquxinxi);
        louzhuangdiaocha = this.findViewById(R.id.data_louzhuangdiaocha);
        louzhuanxinxi = this.findViewById(R.id.data_louzhuangxinxi);

        initDialog();
        //添加监听器
        xiaoqugaikuang.setOnClickListener(new DataListener(this, ConstTools.XIAOQUGAIKUANG, DataActivity.this));
        xiaoquxinxi.setOnClickListener(new DataListener(this, ConstTools.XIAOQUXINXI, DataActivity.this));
        louzhuanxinxi.setOnClickListener(new DataListener(this, ConstTools.LOUZHUANGXINXI, DataActivity.this));
//        louzhuangdiaocha.setOnClickListener(new DataListener(this, ConstTools.LOUZHUANGDIAOCHA, DataActivity.this));
        louzhuangdiaocha.setOnClickListener(louzhuangdiaochaListener);
    }

    private void initDialog(){
        BuildNumView = getLayoutInflater().inflate(R.layout.building_num_post, null);
        buildingNumDialog = CreatDialog.createChangeMarkDialog(DataActivity.this, BuildNumView);
        buttonSubmit = BuildNumView.findViewById(R.id.building_num_submit);
        buttonCancel = BuildNumView.findViewById(R.id.building_num_cancel);
        editBuildingNum = BuildNumView.findViewById(R.id.building_num_edit);
    }
     View.OnClickListener louzhuangdiaochaListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buildingNumDialog.show();
            buttonSubmit.setOnClickListener(subminListener);
            buttonCancel.setOnClickListener(cancelListener);
        }
    };

    View.OnClickListener subminListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(DataActivity.this,"提交测试成功",Toast.LENGTH_SHORT).show();

            buildingNumDialog.dismiss();
        }
    };
     View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(DataActivity.this,"取消测试成功",Toast.LENGTH_SHORT).show();
            buildingNumDialog.dismiss();
        }
    };
}
