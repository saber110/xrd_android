package com.example.collectdata_01;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;

import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.model.TImage;
import org.devio.takephoto.model.TResult;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


//takephotoActivity是一个专门用来拍照并且存储和涂鸦的类
public class takePhoto01 extends TakePhotoActivity {

    //定义了一些参数
    private int yourChoice;
    private String item;
    private Intent intent;
    private int gardenId;
    private String pictureKind;

    private boolean pmflag = true;
    private boolean lmflag = true;
    /** 数据库引用对象 */
    static DataBase mainDB;

    static LiteOrm liteOrm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            showSingleChoiceDialog();
    }


    //以下是重写了方法
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

    public String locationString;
    public String jpegName;

    //拍照方法
    private void picture(){
        if(locationString.equals("平面")){
            pictureKind = Integer.toString(2);
            if(pmflag){
                jpegName = "2_小区平面图_001"  +".jpg";
                pmflag = false;
            }
            else {
                jpegName = "2_小区平面图_002" + ".jpg";
                pmflag = true;
            }
        }
        if(locationString.equals("入口")){
            jpegName = "2_小区入口_001"  +".jpg";
            pictureKind = Integer.toString(2);
        }
        if(locationString.equals("外")){
            jpegName = "2_小区外景图_001"  +".jpg";
            pictureKind = Integer.toString(2);
        }
        if(locationString.equals("内")){
            jpegName = "2_小区内景图_001"  +".jpg";
            pictureKind = Integer.toString(2);
        }
        if(locationString.equals("楼号")){
            jpegName = "3_小区楼牌号_001"  +".jpg";
            pictureKind = Integer.toString(3);
        }
        else{
            pictureKind = Integer.toString(3);
            if(lmflag){
                jpegName = "3_小区建筑立面_001" + locationString +".jpg";
                lmflag = false;
            }
            else {
                jpegName = "3_小区建筑立面_002" + locationString +".jpg";
                lmflag = true;
            }
        }

        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + jpegName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        final Uri imageUri = Uri.fromFile(file);
        getTakePhoto().onPickFromCapture(imageUri);

        // 创建数据库
        if (mainDB == null) {
            // 创建数据库,传入当前上下文对象和数据库名称
            mainDB = LiteOrm.newSingleInstance(this, "imageData.db");
            System.out.println("数据库创建成功");
        }

        Users musers = new Users("1", pictureKind, Integer.toString((int) System.currentTimeMillis()), Integer.toString(12345678), jpegName);
        System.out.println("用户创建成功");
        mainDB.save(musers);
        System.out.println("保存数据成功");
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
                        locationString = item;
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
                                locationString = edit.getText().toString();
                                //调用拍照方法
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

                            //若选择了立面则立马跳出来自定义对话框
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
                            locationString = "平面";
                            picture();
                        }
                    }
                });
        singleChoiceDialog.show();
    }

    /**
     * 单选对话框
     */
    private void singleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(takePhoto01.this);
        builder.setTitle("是否对您当前的图片进行操作");
        final String[] items = { "是", "否" };// 创建一个存放选项的数组
        final boolean[] checkedItems = { true, false };// 存放选中状态，true为选中
        // ，false为未选中，和setSingleChoiceItems中第二个参数对应
        // 为对话框添加单选列表项
        // 第一个参数存放选项的数组，第二个参数存放默认被选中的项，第三个参数点击事件
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                }
                checkedItems[arg1] = true;
            }
        });
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface arg0, int arg1) {
//                // TODO Auto-generated method stub
//                showSingleChoiceDialog();
//                arg0.dismiss();
//            }
//        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                String str = "";
                for (int i = 0; i < checkedItems.length; i++) {
                    if (checkedItems[i]) {
                        str = items[i];
                    }
                }
                Toast.makeText(takePhoto01.this,"你选择了" + str, Toast.LENGTH_SHORT).show();
                if(str.equals("是")){
                    startActivity(new Intent(takePhoto01.this, DrawActivity.class));
                }
                else picture();
            }
        });
        builder.create().show();
    }


    private void showImg(ArrayList<TImage> images) {
        singleDialog();
//        Intent intent = new Intent(takePhoto01.this, MainActivity.class);
////        intent.putExtra("images", images);
//        startActivity(intent);
        Toast.makeText(takePhoto01.this, "已存储", Toast.LENGTH_SHORT).show();
    }
}
