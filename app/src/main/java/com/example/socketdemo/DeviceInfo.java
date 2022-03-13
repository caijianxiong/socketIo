package com.example.socketdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

public class DeviceInfo {
    private static String TAG = "DeviceInfo";

    public static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
            Log.i(TAG, "getSerialNumber: " + serial);
        } catch (Exception e) {
            Log.e(TAG, "getSerialNumber: error", e);
        }
        return serial;
    }

    public static String getIMEI(Context context) {
        String sn = "null";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sn = Build.getSerial();
        } else {
            sn = Build.SERIAL;
        }
        return sn;
    }


    public static String getDeviceSN(){
        String serialNumber = android.os.Build.SERIAL;
        Log.i(TAG, "getDeviceSN: "+serialNumber);
        return serialNumber;
    }


}

