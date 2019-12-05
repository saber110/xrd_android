package com.example.deviceId;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class getSN {

    private static String SN;

    public static String getAndroidId(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    @SuppressLint("MissingPermission")
    public static String getSerialNumber() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            return android.os.Build.getSerial();
        else
            return android.os.Build.SERIAL;
    }
}