package com.example.collectdata_01;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class TakePhoto extends AppCompatActivity
{

    private static final int MSG_AUTOFUCS = 1001;
    private static final String TAG = "shiren";

    AutoFocusCallbackImpl autoFocusCallback;
    Camera.ShutterCallback mShutterCallback;
    Camera.PictureCallback mRawCallback;
    Camera.PictureCallback mJpegPictureCallback;

    private Camera mCamera;
    private SurfaceView videoview;
    private SurfaceView dynamicview;
    private Handler handler;

    FileUtil fu = new FileUtil();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showSingleChoiceDialog();
        setContentView(R.layout.activity_take_photo);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Log.v("zzw",""+msg.what);
                switch (msg.what){
                    case MSG_AUTOFUCS:
                        if(mCamera != null){
                            System.out.println("handleMessage::" + TakePhoto.this + "\t" + mCamera);
                            mCamera.autoFocus(autoFocusCallback);
                        }

                        break;
                }
            }
        };
        videoview = findViewById(R.id.surfaceView);
        ImageButton tokenbutton = findViewById(R.id.takephoto);
        initTakePictureCallback();
        initButtonlistener(tokenbutton);
        initAutoFocusCallback();
        initSurfaceView(videoview);
    }







    int yourChoice;
    String item;
    private void showSingleChoiceDialog(){
        final String[] items = { "平面","入口","外","内","立面","楼号"};
        yourChoice = -1;
        AlertDialog.Builder singleChoiceDialog =
                new AlertDialog.Builder(TakePhoto.this);
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
                        final Context context = TakePhoto.this;
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
                            Toast.makeText(TakePhoto.this,
                                    "你选择了" + items[yourChoice],
                                    Toast.LENGTH_SHORT).show();

                            //若选择了里面则立马跳出来自定义对话框
                            if(items[yourChoice].equals("立面")){
                                builder.setCancelable(true);    //设置按钮是否可以按返回键取消,false则不可以取消
                                AlertDialog dialoga = builder.create();  //创建对话框
                                dialoga.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
                                dialoga.show();
                            }
                        }
                        //可能没按选择，默认为平面
                        else {
                            Toast.makeText(TakePhoto.this,
                                    "你选择了平面",
                                    Toast.LENGTH_SHORT).show();
                            fu.locationString = "平面";
                        }
                    }
                });
        singleChoiceDialog.show();
    }



    /**
     * @method 初始化OnTouchListener
     * @param button
     */
    public void initButtonlistener(ImageButton button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCamera != null){
//                        takePhotoThenStorage();
                    System.out.println("initButtonlistener::" + this + "\t" + mCamera);
                    mCamera.takePicture(mShutterCallback, mRawCallback, mJpegPictureCallback);
                }
            }
        });
    }
    /**
     *@method 初始化回调函数
     */
    public void initTakePictureCallback(){
        /*为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量*/
        mShutterCallback = new Camera.ShutterCallback()
                //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
        {
            public void onShutter() {
                // TODO Auto-generated method stub
                Log.i(TAG, "myShutterCallback:onShutter...");
            }
        };
        mRawCallback = new Camera.PictureCallback()
                // 拍摄的未压缩原数据的回调,可以为null
        {

            public void onPictureTaken(byte[] data, Camera camera) {
                // TODO Auto-generated method stub
                Log.i(TAG, "myRawCallback:onPictureTaken...");
            }
        };
        mJpegPictureCallback = new Camera.PictureCallback()
                //对jpeg图像数据的回调,最重要的一个回调
        {
            public void onPictureTaken(final byte[] data, Camera camera) {
                // TODO Auto-generated method stub
                Log.i(TAG, "myJpegCallback:onPictureTaken...");
                new Thread(){
                    public void run(){
                        Bitmap b = null;
                        if(null != data){
                            b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
                            // mCamera.stopPreview();
                        }
                        //保存图片到sdcard
                        if(null != b)
                        {
                            //设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation", 90)失效。
                            //图片竟然不能旋转了，故这里要旋转下
                            Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);

                            fu.saveBitmap(rotaBitmap);
                        }
                        //再次进入预览
                        mCamera.startPreview();
                    }
                }.start();
            }
        };

    }
    /**
     * @method 初始化AutoFocusCallbackImpl
     */
    public void initAutoFocusCallback(){
        autoFocusCallback = new AutoFocusCallbackImpl();
        autoFocusCallback.setHandler(handler,MSG_AUTOFUCS);
    }
    /**
     * @method 初始化SurfaceView，开启Camera
     * @param videoview
     */
    public void initSurfaceView(final SurfaceView videoview){
        videoview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                //thread.start();
                openCamera();
                //自动对焦
                mCamera.autoFocus(autoFocusCallback);
                //autoImage(videoview.getWidth()/2,videoview.getHeight()/2,20);
            }
            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                System.out.println("surfaceDestroyed::" + this + "\t" + mCamera);
                if(mCamera != null){
                    mCamera.stopPreview();
                    mCamera.release();
                }

                //thread.interrupt();
            }
        });
    }
    /**
     * @method
     */
//        public void autoImage(float x,float y,float radius){
//            Canvas canvas = dynamicview.getHolder().lockCanvas();
//            Paint paint = new Paint();
//            paint.setColor(Color.green(1));
//            canvas.drawCircle(x,y,radius,paint);
//            dynamicview.getHolder().unlockCanvasAndPost(canvas);
//        }
    /**
     * @method 照相
     */
//        public void takePhotoThenStorage(){
//            mCamera.takePicture(null, null, new Camera.PictureCallback() {
//                @Override
//                public void onPictureTaken(byte[] data, Camera camera) {
//                    //将字节数组
//                    Bitmap bitmap= BitmapFactory.decodeByteArray(data,0,data.length);
//                    //输出流保存数据
//                    try {
//                        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//                        FileOutputStream fileOutputStream=new FileOutputStream("/mnt/sdcard/DCIM/camera/VideoApp"+df.format(new Date())+".png");
//                        bitmap.compress(Bitmap.CompressFormat.PNG,85,fileOutputStream);
//                        camera.stopPreview();
//                        camera.startPreview();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
    /**
     * @method 打开摄像头
     */
    private void openCamera() {
        if (mCamera != null) {
            throw new RuntimeException("相机已经被开启，无法同时开启多个相机实例！");
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (hasBackFacingCamera()) {
                // 优先开启后置摄像头
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                try {
                    changeParam(mCamera);
                    mCamera.setPreviewDisplay(videoview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCamera.startPreview();
            } else if (hasBackFacingCamera()) {
                // 没有前置，就尝试开启前置摄像头
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                try {
                    changeParam(mCamera);
                    mCamera.setPreviewDisplay(videoview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCamera.startPreview();
            } else {
                throw new RuntimeException("没有任何相机可以开启！");
            }
        }
    };
    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
    }
    @Override
    protected void  onDestroy() {
        super.onDestroy();
        if(thread != null){
            thread.interrupt();
            thread = null;
        }
        if(mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
    /**
     * @method 关闭相机
     */
    private void closeCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
//        /**
//         * @method 添加摄像头回调方法
//         * @param mCamera
//         */
//        public void addCallBack(Camera mCamera){
//            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
//                @Override
//                public void onPreviewFrame(byte[] bytes, Camera camera) {
//                    ByteArrayOutputStream bas = new ByteArrayOutputStream();
//                    YuvImage yuv = new YuvImage(bytes, ImageFormat.NV21,videoview.getWidth(),videoview.getHeight(),null);
//                    yuv.compressToJpeg(new Rect(),1,bas);
//                    byte[] byts = bas.toByteArray();
//                }
//            });
//        }
    /**
     * @method 设置摄像头的参数
     * @param  mCamera
     */
    public void changeParam(Camera mCamera){
        Camera.Parameters param = mCamera.getParameters();
//        param.setZoom(2);
//        WindowManager windowManager = getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        int screenWidth =  display.getWidth();
//        int screenHeight = display.getHeight();
//        param.setPreviewSize(400,240);
        mCamera.setParameters(param);
        mCamera.setDisplayOrientation(90);
    }

    /**
     * @method 获取摄像头个数并判断是否有无前置或后置摄像头
     * @param facing CAMERA_FACING_BACK || CAMERA_FACING_FRONT
     * @return true
     */
    private static boolean checkCameraFacing(final int facing) {
        if (getSdkVersion() < Build.VERSION_CODES.GINGERBREAD) {
            return false;
        }
        final int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (facing == info.facing) {
                return true;
            }
        }
        return false;
    }
    /**
     * @method 判断有无后置摄像头
     * @return true
     */
    public static boolean hasBackFacingCamera() {
        final int CAMERA_FACING_BACK = 0;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }
    /**
     * @method 判断有无前置摄像头
     * @return true
     */
//        public static boolean hasFrontFacingCamera() {
//            final int CAMERA_FACING_FRONT = 1;
//            return checkCameraFacing(CAMERA_FACING_FRONT);
//        }
    /**
     * @method 返回版本信息
     * @return int
     */
    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }
    /**
     * @Attribute thread
     */
    public  Thread thread = new Thread(){
        Paint paint = new Paint();
        @Override
        public void run(){
//                float x = 100, y = 100;
            while(!interrupted()){
//                    Canvas canvas = null;
//                    canvas = videoview.getHolder().lockCanvas();
//                    paint.setStyle(Paint.Style.FILL);
//                    canvas.drawText("@author/wang",x++,y++,paint);
//                    videoview.getHolder().unlockCanvasAndPost(canvas);
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
            }
        }
    };

}



