package com.example.collectdata_01;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectdata_01.adapter.DatalistAdapter;
import com.example.collectdata_01.util.UploadImgUtil;
import com.example.database.ImageDb;
import com.example.database.UsingNeighbourDb;
import com.example.login.login;
import com.google.android.material.button.MaterialButton;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.collectdata_01.MainActivity.mainDB;

public class Datalist extends AppCompatActivity {
    ArrayList<ImageDb> gardenlist = new ArrayList<>();
    ArrayList<ImageDb> buildinglist = new ArrayList<>();
    ArrayList<ImageDb> qitalist = new ArrayList<>();
    private MaterialButton uploadPictures;
    public static DatalistAdapter adapter;
    public static HashMap<String,ArrayList<String>> pictures = new HashMap<>();
    private HashMap<String,Boolean> isClicked = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datalist);
        uploadPictures = findViewById(R.id.uploadPictures);

        RecyclerView listView = findViewById(R.id.list);
        final List<String> list = new ArrayList<String>();
        //要添加的内容直接添加到list 队列里面就可显示出来   如
//        list.add("数据一");
        dividedData(list,true);
        for(String s:list) isClicked.put(s,false);
        ///可以一直添加，在真机运行后可以下拉列表
        adapter = new DatalistAdapter(this, R.layout.datalist_item, list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setmOnItemClickListener(new DatalistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position,boolean flag) {
                // 点击到小区名
                if(flag&&!isClicked.get(list.get(position))){
                    String id = list.get(position);
                    for(String name:pictures.get(id)){
                        list.add(position+1,name);
                    }
                    isClicked.put(id,true);
                    adapter.notifyDataSetChanged();
                }
                // 点击到照片
                else if (!flag&&adapter.getResultMap().keySet().contains(list.get(position))) {
                    Intent i1 = new Intent(getApplicationContext(), ImageActivity.class);
                    i1.putExtra("image", list.get(position));
                    startActivity(i1);
                }
                else System.out.println(flag+" "+list.get(position));
            }

            @Override
            public void onItemLongClick(int position){
                if (adapter.getResultMap().keySet().contains(list.get(position))) {
                    alert_edit(position, list);
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
                    uploadImgUtil.uploadBuildImg(buildinglist.get(i).getBuildingName(), buildinglist.get(i).getCollectTime(), buildinglist.get(i).getGardenId(), buildinglist.get(i).getpictureKind(), buildinglist.get(i).getImage());
        }
        for (int i = 0; i < qitalist.size(); i++) {
            if (result.keySet().contains(qitalist.get(i).getImage()))
                if (result.get(qitalist.get(i).getImage()))
                    uploadImgUtil.uploadOtherImg(qitalist.get(i).getGardenId(), qitalist.get(i).getCollectTime(), login.token, qitalist.get(i).getImage());
        }
    }

    private void dividedData(List<String> listView,boolean flag) {
        gardenlist.clear();
        buildinglist.clear();
        qitalist.clear();
        ArrayList<ImageDb> queryList = mainDB.query(new QueryBuilder<ImageDb>(ImageDb.class)
                .whereEquals(ImageDb.ISUPLOADED_COL, false)
                .appendOrderDescBy(ImageDb.GARDENID_COL)
                .appendOrderDescBy(ImageDb.PICTUREKIND_COL)
                .appendOrderDescBy(ImageDb.BUILDINGNAME_COL));
        String id="-1";
        for (int i = 0; i < queryList.size(); i++) {
            // 检查文件是否存在
            // 若已经被删除，或者不存在
            // 删除该记录
            if(! fileIsExists(Environment.getExternalStorageDirectory()+ "/"+ getResources().getString(R.string.picturePath) + "/" + queryList.get(i).getImage()))
            {
                mainDB.delete(queryList.get(i));
                queryList.remove(i);
            }
            if (queryList.get(i).getpictureKind().equals(getResources().getString(R.string.pingMianTu))
                    || queryList.get(i).getpictureKind().equals(getResources().getString(R.string.xiaoQuRuKou))
                    || queryList.get(i).getpictureKind().equals(getResources().getString(R.string.waiJingTu))
                    || queryList.get(i).getpictureKind().equals(getResources().getString(R.string.neiJingTu))) {
                gardenlist.add(queryList.get(i));
            }
            if (queryList.get(i).getpictureKind().equals(getResources().getString(R.string.jianZhuLiMian))
                    || queryList.get(i).getpictureKind().equals(getResources().getString(R.string.zhuangPaiHao))) {
                buildinglist.add(queryList.get(i));
            }

            // 复用上传其他照片的接口上传涂鸦照片
            if (queryList.get(i).getpictureKind().equals(getResources().getString(R.string.qiTa))
                || queryList.get(i).getpictureKind().equals(getResources().getString(R.string.tuYa))) {
                qitalist.add(queryList.get(i));
            }
            if(flag) {
                String nowId = queryList.get(i).getGardenId();
                if (!nowId.equals(id)) {
                    id = nowId;
//                ArrayList<ImageDb> queryGardenName = mainDB.query(new QueryBuilder<ImageDb>(ImageDb.class)
//                        .whereEquals(ImageDb.GARDENID_COL, id));
//                listView.add(queryGardenName.get(0).getGardenName());
                    listView.add(id);
                    pictures.put(id, new ArrayList<String>());
                }
                //listView.add(queryList.get(i).getImage());
                pictures.get(nowId).add(queryList.get(i).getImage());
            }
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

                        if (newName.equals(oldName)) {
                            Toast.makeText(getApplicationContext(),"新名称不能与旧名称相同！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (list.contains(newName)) {
                            Toast.makeText(getApplicationContext(),"此名称已存在！",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for(String id:pictures.keySet()){
                            ArrayList<String> arrayList = pictures.get(id);
                            for(int j=0;j<arrayList.size();j++){
                                if(arrayList.get(j).equals(oldName)){
                                    arrayList.remove(oldName);
                                    arrayList.add(newName);
                                }
                            }
                        }
                        boolean b;
                        b = adapter.getResultMap().get(oldName);
                        adapter.getResultMap().remove(oldName);
                        setNameByCollecttime(oldName, newName);
                        FileRename(oldName,newName);
                        list.remove(pos);
                        list.add(pos, newName);
                        adapter.getResultMap().put(list.get(pos), b);
                        dividedData(list,false);
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
        ArrayList<ImageDb> updateUser = mainDB.query(new QueryBuilder<ImageDb>(ImageDb.class)
                .whereEquals(ImageDb.IMAGE_COL , oldName));
        updateUser.get(0).setImage(newName);
        ColumnsValue cv = new ColumnsValue(new String[]{ImageDb.IMAGE_COL});
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

    /**
     * 检测文件是否存在
     * @param strFile
     * @return false when none
     */
    public boolean fileIsExists(String strFile)
    {
        try
        {
            File f = new File(strFile);
            if(!f.exists())
            {
                return false;
            }

        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }
}

