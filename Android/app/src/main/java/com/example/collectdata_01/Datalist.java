package com.example.collectdata_01;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectdata_01.adapter.DatalistAdapter;
import com.example.collectdata_01.util.UploadImgUtil;
import com.example.login.login;
import com.google.android.material.button.MaterialButton;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.collectdata_01.MainActivity.mainDB;

public class Datalist extends AppCompatActivity {
    ArrayList<Users> gardenlist = new ArrayList<>();
    ArrayList<Users> buildinglist = new ArrayList<>();
    ArrayList<Users> qitalist = new ArrayList<>();
    private MaterialButton uploadPictures;
    private DatalistAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datalist);
        final TextView textView = (TextView) findViewById(R.id.text);
        uploadPictures = findViewById(R.id.uploadPictures);

        RecyclerView listView = findViewById(R.id.list);
        final List<String> list = new ArrayList<String>();
        //要添加的内容直接添加到list 队列里面就可显示出来   如
//        list.add("数据一");

        dividedData(list);

        ///可以一直添加，在真机运行后可以下拉列表
        adapter = new DatalistAdapter(this, R.layout.datalist_item, list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setmOnItemClickListener(new DatalistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (adapter.getResultMap().keySet().contains(list.get(position))) {
                    alert_edit(position, list);
//                    Toast.makeText(Datalist.this, "点击" + list.get(position), Toast.LENGTH_SHORT).show();
                }
            }
        });

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
        HashMap<String,Boolean> result = adapter.getResultMap();
        for (int i = 0; i < gardenlist.size(); i++) {
            if (result.keySet().contains(gardenlist.get(i).getImage()))
                if (result.get(gardenlist.get(i).getImage()))
                    uploadImgUtil.uploadGardenImg(gardenlist.get(i).getGardenId(), gardenlist.get(i).getpictureKind(), gardenlist.get(i).getCollectTime(), login.token, gardenlist.get(i).getImage());
        }
        for (int i = 0; i < buildinglist.size(); i++) {
            if (result.keySet().contains(buildinglist.get(i).getImage()))
                if (result.get(buildinglist.get(i).getImage()))

                    uploadImgUtil.uploadBuildImg(buildinglist.get(i).getBuildingName(), buildinglist.get(i).getCollectTime(), Integer.toString(MainActivity.getGardenId()), buildinglist.get(i).getpictureKind(), buildinglist.get(i).getImage());
        }
        for (int i = 0; i < qitalist.size(); i++) {
            if (result.keySet().contains(qitalist.get(i).getImage()))
                if (result.get(qitalist.get(i).getImage()))
                    uploadImgUtil.uploadOtherImg(qitalist.get(i).getGardenId(), qitalist.get(i).getCollectTime(), login.token, qitalist.get(i).getImage());
        }
    }

    private void dividedData(List<String> listView) {
        gardenlist.clear();
        buildinglist.clear();
        qitalist.clear();
        ArrayList<Users> list = mainDB.query(new QueryBuilder<Users>(Users.class)
                .whereEquals(Users.ISUPLOADED_COL, false)
                .appendOrderAscBy(Users.GARDENID_COL)
                .appendOrderAscBy(Users.PICTUREKIND_COL)
                .appendOrderAscBy(Users.BUILDINGNAME_COL));
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getpictureKind().equals(getResources().getString(R.string.pingMianTu))
                    || list.get(i).getpictureKind().equals(getResources().getString(R.string.xiaoQuRuKou))
                    || list.get(i).getpictureKind().equals(getResources().getString(R.string.waiJingTu))
                    || list.get(i).getpictureKind().equals(getResources().getString(R.string.neiJingTu))) {
                gardenlist.add(list.get(i));
            }
            if (list.get(i).getpictureKind().equals(getResources().getString(R.string.jianZhuLiMian))
                    || list.get(i).getpictureKind().equals(getResources().getString(R.string.zhuangPaiHao))) {
                buildinglist.add(list.get(i));
            }

            // 复用上传其他照片的接口上传涂鸦照片
            if (list.get(i).getpictureKind().equals(getResources().getString(R.string.qiTa))
                || list.get(i).getpictureKind().equals(getResources().getString(R.string.tuYa))) {
                qitalist.add(list.get(i));
            }
            listView.add(list.get(i).getImage());
        }
    }

    // 弹出dialog修改文件名字
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
                        setNameByCollecttime(oldName, newName);
                        FileRename(oldName,newName);
                        list.remove(pos);
                        list.add(pos, newName);
                        adapter.getResultMap().put(list.get(pos), b);
                        adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("取消", null).show();
    }

    private void FileRename(String oldName, String newName) {
        File file = new File(Environment.getExternalStorageDirectory(), "/"+ getResources().getString(R.string.picturePath) + "/" + oldName);
        File to = new File(Environment.getExternalStorageDirectory(), "/"+ getResources().getString(R.string.picturePath) + "/" + newName);
        file.renameTo(to);
        saveToSystemAlbum(to);
    }


    public void setNameByCollecttime(String oldName,String newName){
        // 设置数据库中的上传控制项
        // collectTime唯一
        ArrayList<Users> updateUser = mainDB.query(new QueryBuilder<Users>(Users.class)
                .whereEquals(Users.IMAGE_COL , oldName));
        updateUser.get(0).setImage(newName);
        ColumnsValue cv = new ColumnsValue(new String[]{Users.IMAGE_COL});
        mainDB.update(updateUser.get(0), cv, ConflictAlgorithm.None);
    }

    /**
     * 将拍照的图片加入系统相册中
     *
     * @param file
     */
    private void saveToSystemAlbum(File file) {
        //其次把文件插入到系统图库
        try {
            Log.i("update album1", "picture: " + file.getAbsolutePath());

            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
            Log.i("update album", "picture: " + file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 通知图库更新
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                            sendBroadcast(mediaScanIntent);
                        }
                    });
        } else {
            String relationDir = file.getParent();
            File file1 = new File(relationDir);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file1.getAbsoluteFile())));
        }
    }
}

