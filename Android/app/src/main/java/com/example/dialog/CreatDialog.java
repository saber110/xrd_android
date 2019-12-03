package com.example.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

/**
 * 选择地图和小区id的提示框
 */
public class CreatDialog {

    /**
     * 创建一个dialog
     * @param context
     * @return
     */
    public static Dialog createSendMapDataDialog(Context context, View view) {
        final Dialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create();

        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM);
        win.getDecorView().setPadding(10, 0, 10, 10);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setBackgroundDrawable(null);
        win.setAttributes(lp);
        return  dialog;
    }


    public static Dialog createChangeMarkDialog(Context context, View view) {
        final Dialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create();

        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM);
        win.getDecorView().setPadding(10, 0, 10, 10);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setBackgroundDrawable(null);
        win.setAttributes(lp);
        return  dialog;
    }

    /**
     * 城市选择器
     * @param context
     * @param view
     * @return
     */
    public static Dialog createLocationSelectDialog(Context context, View view) {
        final Dialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create();

        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM);
        win.getDecorView().setPadding(10, 0, 10, 10);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setBackgroundDrawable(null);
        win.setAttributes(lp);
        return  dialog;
    }

    /**
     * loading
     */
//    public static Dialog loadingDialog(Context context) {
//
//        final Dialog dialog = new AlertDialog.Builder(context)
//                .setView(view)
//                .setCancelable(true)
//                .create();
//
//        Window win = dialog.getWindow();
//        win.setGravity(Gravity.CENTER);
//        win.getDecorView().setPadding(10, 0, 10, 10);
//        WindowManager.LayoutParams lp = win.getAttributes();
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        dialog.getWindow().setBackgroundDrawable(null);
//        win.setAttributes(lp);
//        return  dialog;
//    }
}
