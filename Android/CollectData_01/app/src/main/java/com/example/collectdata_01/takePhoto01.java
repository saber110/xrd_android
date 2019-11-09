package com.example.collectdata_01;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.model.TImage;
import org.devio.takephoto.model.TResult;

import java.io.File;
import java.util.ArrayList;

public class takePhoto01 extends TakePhotoActivity {

    FileUtil fu = new FileUtil();
    int yourChoice;
    String item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showSingleChoiceDialog();

    }


    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        showImg(result.getImages());
    }

    private void picture(){
        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        final Uri imageUri = Uri.fromFile(file);
        getTakePhoto().onPickFromCapture(imageUri);
    }

    private void showSingleChoiceDialog(){
        final String[] items = { "平面","入口","外","内","立面","楼号"};
        yourChoice = -1;
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(takePhoto01.this);
        singleChoiceDialog.setTitle("选择你要拍摄的区域");
        // 第二个参数是默认选项，此处设置为0
        singleChoiceDialog.setSingleChoiceItems(items, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yourChoice = which;
                        item = items[yourChoice];
                        fu.locationString = item;
                    }
                });
        singleChoiceDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //自定义对话框
                        final Context context = takePhoto01.this;
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("请输入地点");    //设置对话框标题
                        builder.setIcon(R.drawable.icon_cricle);   //设置对话框标题前的图标
                        final EditText edit = new EditText(context);

                        builder.setView(edit);
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "你确立的地点是: " + edit.getText().toString(), Toast.LENGTH_SHORT).show();
                                //把输入的地点赋给 fu.locationString
                                fu.locationString = edit.getText().toString();
                                picture();
                            }
                        });
//                            builder.setPositiveButton("不清楚", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Toast.makeText(context, "遗憾，你不知道这个地方: " + edit.getText().toString(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "你取消输入", Toast.LENGTH_SHORT).show();
                            }
                        });
                        if (yourChoice != -1) {
                            Toast.makeText(takePhoto01.this,"你选择了" + items[yourChoice], Toast.LENGTH_SHORT).show();

                            //若选择了里面则立马跳出来自定义对话框
                            if(items[yourChoice].equals("立面")){
                                builder.setCancelable(true);    //设置按钮是否可以按返回键取消,false则不可以取消
                                AlertDialog dialoga = builder.create();  //创建对话框
                                dialoga.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
                                dialoga.show();
                            }
                            else {
                                picture();
                            }
                        }
                        //可能没按选择，默认为平面
                        else {
                            Toast.makeText(takePhoto01.this,
                                    "你选择了平面",
                                    Toast.LENGTH_SHORT).show();
                            fu.locationString = "平面";
                            picture();
                        }
                    }
                });
        singleChoiceDialog.show();
    }


    private void showImg(ArrayList<TImage> images) {
        Intent intent = new Intent(takePhoto01.this, MainActivity.class);
//        intent.putExtra("images", images);
        startActivity(intent);
        Toast.makeText(takePhoto01.this, "已存储", Toast.LENGTH_SHORT).show();

    }
}
