package com.example.collectdata_01;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
    private static final  String TAG = "FileUtil";
    private static final File parentPath = Environment.getExternalStorageDirectory();
    private static   String storagePath = "";
    private static final String DST_FOLDER_NAME = "PlayCamera";

    /**初始化保存路径
     * @return
     */
    private static String initPath(){
        if(storagePath.equals("")){
            storagePath = parentPath.getAbsolutePath()+"/" + DST_FOLDER_NAME;
            File f = new File(storagePath);
            if(!f.exists()){
                f.mkdir();
            }
        }
        return storagePath;
    }

    /**保存Bitmap到sdcard
     * @param b
     */
    //用获取到的位置信息保存图片
    String locationString;
    String jpegName;
    public void saveBitmap(Bitmap b){

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        long dataTake = System.currentTimeMillis();

        if(locationString.equals("平面")){
            jpegName = path + "/" + "2_平面图_001"  +".jpg";
        }
        if(locationString.equals("入口")){
            jpegName = path + "/" + "2_小区入口_001"  +".jpg";
        }
        if(locationString.equals("外")){
            jpegName = path + "/" + "2_外景图_001"  +".jpg";
        }
        if(locationString.equals("内")){
            jpegName = path + "/" + "2_内景图_001"  +".jpg";
        }
        if(locationString.equals("楼号")){
            jpegName = path + "/" + "3_楼牌号_001"  +".jpg";
        }
        else{
            jpegName = path + "/" + "3_建筑立面_001"  +".jpg";
        }

        Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
            Log.i(TAG, "saveBitmap成功");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.i(TAG, "saveBitmap:失败");
            e.printStackTrace();
        }
    }
}