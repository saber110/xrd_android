package com.example.collectdata_01.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class DrawUtil {

    public  static  Bitmap drawBitMap(String str,float scale) {
        Bitmap bitmap;
        int width = 100;
        int height = 100;
        bitmap = Bitmap.createBitmap(width*str.length()==0?100:width*str.length(), height, Bitmap.Config.ARGB_4444); //建立一个空的Bitmap
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿
        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤
        paint.setColor(Color.RED);
        paint.setTextSize(14 * scale);

        Rect bounds = new Rect();
        paint.getTextBounds(str, 0, str.length(), bounds);//获取文字的范围
        //文字在mMarker中展示的位置
        float paddingLeft = (bitmap.getWidth() - bounds.width()) / 2;//在中间
        float paddingTop = (bitmap.getHeight() / scale);//在顶部

        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(str, paddingLeft, paddingTop, paint);
        return bitmap;
    }

}
