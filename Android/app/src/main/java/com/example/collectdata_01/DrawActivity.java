package com.example.collectdata_01;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.database.ImageDb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.hzw.doodle.DoodleActivity;
import cn.hzw.doodle.DoodleParams;

public class DrawActivity extends AppCompatActivity {

    Paint paint = new Paint();
    Canvas canvas = null;
    String str = "";
    Bitmap bitmap = null;
    ImageView imageview;
    int count = 0;
    //    Button choose_photo = this.findViewById(R.id.get);
    public static final int PICK_PHOTO = 102;
    public static final int REQ_CODE_DOODLE = 101;

    /**
     * 是否继续画图对话框
     */
    private void singleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DrawActivity.this);
        builder.setTitle("是否继续画图");
        final String[] items = { "是", "否" };// 创建一个存放选项的数组
        final boolean[] checkedItems = { true, false };// 存放选中状态，true为选中
        // ，false为未选中，和setSingleChoiceItems中第二个参数对应
        // 为对话框添加单选列表项
        // 第一个参数存放选项的数组，第二个参数存放默认被选中的项，第三个参数点击事件
        builder.setSingleChoiceItems(items, 1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                }
                checkedItems[arg1] = true;
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                startActivity(new Intent(DrawActivity.this, MainActivity.class));
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                final Context context = DrawActivity.this;
                String str = "";
                for (int i = 0; i < checkedItems.length; i++) {
                    if (checkedItems[i]) {
                        str = items[i];
                    }
                }
                if(str.equals("是")){
                    Toast.makeText(context, "请继续作图或者选择导入图片", Toast.LENGTH_SHORT).show();
                }
                if(str.equals("否")) {
                    Toast.makeText(context, "返回主页", Toast.LENGTH_SHORT).show();
                    finish();
                }
                //默认
                else {
                    Toast.makeText(context, "返回主页", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        builder.create().show();

    }

    public void onBtnClick(View v){
        Button btn = (Button)v;
        str = btn.getText().toString();

        if(str.equals("保存")){
            String[] PERMISSIONS = {
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE" };
            //检测是否有写的权限
            int permission = ContextCompat.checkSelfPermission(DrawActivity.this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(DrawActivity.this, PERMISSIONS,1);
            }

            if(!flag){
                saveBitmap(bitmap2 ,getResources().getString(R.string.tuYa)+ "_" + jpeg);

            }
            singleDialog();
        }
        else if(str.equals("导入")){

            if (ContextCompat.checkSelfPermission(DrawActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DrawActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            } else {
                //打开相册
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //Intent.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO); // 打开相册
                count++;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_PHOTO:
                if (resultCode == RESULT_OK) { // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                File file = new File(imagePath);
                jpeg = file.getName();
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
                File file = new File(imagePath);
                jpeg = file.getName();
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
            File file = new File(imagePath);
            jpeg = file.getName();
        } else if ("file".equalsIgnoreCase(uri.getScheme())){
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
            File file = new File(imagePath);
            jpeg = file.getName();
        }
        // 根据图片路径显示图片
        displayImage(imagePath);
    }

    /**
     * android 4.4以前的处理方式
     * @param data
     */
    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    Bitmap bitmap2;
    int screenWidth,screenHeight;
    private void displayImage(String imagePath) {
        if (imagePath != null) {

            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);
            screenWidth = size.x;
            screenHeight = size.y;

            BitmapFactory.Options opts = new BitmapFactory.Options();
            // 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
            opts.inJustDecodeBounds = true;
            bitmap = BitmapFactory.decodeFile(imagePath, opts);
            // 得到图片的宽度、高度；
            int imgWidth = opts.outWidth;
            int imgHeight = opts.outHeight;
            // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于等于该比例的最小整数；
            int widthRatio = (int) Math.ceil(imgWidth / (float) screenWidth);
            int heightRatio = (int) Math.ceil(imgHeight / (float) screenHeight);
            opts.inSampleSize = 1;
            if (widthRatio > 1 || heightRatio > 1) {
                if (widthRatio > heightRatio) {
                    opts.inSampleSize = widthRatio;
                } else {
                    opts.inSampleSize = heightRatio;
                }
            }
            // 设置好缩放比例后，加载图片进内容；
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(imagePath, opts);

            bitmap2 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            canvas = new Canvas(bitmap2);
            paint.setDither(true);
            paint.setFilterBitmap(true);
            Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            Rect dst = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, dst, paint);
            imageview.setImageBitmap(bitmap2);
        } else {
            Toast.makeText(this, "获取相册图片失败", Toast.LENGTH_SHORT).show();
            System.out.println("获取相册图片失败");
        }
    }

    //保存
    public void saveBitmap(Bitmap bitmap, String bitName){
        String fileName;
        File file;
        fileName = Environment.getExternalStorageDirectory()+ "/"+ getResources().getString(R.string.picturePath) + "/" + bitName ;

        file = new File(fileName);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out))
            {
                out.flush();
                out.close();
                // 插入图库
                MediaStore.Images.Media.insertImage(this.getContentResolver(), file.getAbsolutePath(), bitName, null);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();

        }
        // 发送广播，通知刷新图库的显示
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
        ImageDb musers = new ImageDb(Integer.toString(MainActivity.getGardenId()),
                getResources().getString(R.string.tuYa),
                Long.toString(System.currentTimeMillis()),
                bitName);
        MainActivity.mainDB.save(musers);
    }


    String jpeg;
    private boolean flag = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        imageview = findViewById(R.id.image);
        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        jpeg = bundle.getString("jpeg");
        String filepath1 = Environment.getExternalStorageDirectory()+ "/"+ getResources().getString(R.string.picturePath) + "/" + jpeg;
        String filepath2 = Environment.getExternalStorageDirectory()+ "/"+ getResources().getString(R.string.picturePath) + "/" + getResources().getString(R.string.tuYa) + "_" + jpeg;

        displayImage(filepath1);
        imageview.setOnTouchListener(new View.OnTouchListener() {
            float x1,y1,x2,y2;
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if(bitmap == null){
                    bitmap = Bitmap.createBitmap(v.getWidth(),v.getHeight(), Bitmap.Config.RGB_565);
                    canvas = new Canvas(bitmap);
                    paint.setColor(Color.WHITE);
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawRect(0f,0f,v.getWidth(),v.getHeight(),paint);
                }
                paint.setColor(Color.RED);
                //线宽度
                paint.setStrokeWidth(5f);
                //实心
                paint.setStyle(Paint.Style.FILL);

                int action = e.getAction();
                if(action == MotionEvent.ACTION_DOWN){
                    x1 = e.getX();
                    y1 = e.getY();
                }
                if(action == MotionEvent.ACTION_MOVE){
                    x2 = e.getX();
                    y2 = e.getY();
                    if(str.equals("开始")){
                        canvas.drawLine(x1,y1,x2,y2,paint);
                        flag = false;
                        x1 = x2;
                        y1 = y2;
                    }
                }
                imageview.invalidate();
                return true;
            }
        });
    }
}
