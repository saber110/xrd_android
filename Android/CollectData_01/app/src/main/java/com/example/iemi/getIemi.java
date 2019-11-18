package com.example.iemi;

import android.content.Context;
import android.telephony.TelephonyManager;

public class getIemi {
    public String IEMI;

    public static final String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);

        String iemi = telephonyManager.getDeviceId();
        return iemi;
    }
}
