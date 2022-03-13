package com.example.socketdemo;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LogUtil {
    private static String TAG="LogUtil";
    private static StringBuffer logBuffer=new StringBuffer();

    private static Handler mMainHandler;

    public static void setNotifyHandler(Handler handler){
        mMainHandler=handler;
    }

    public static void notifyLog(String msg){
        if (mMainHandler==null){
            Log.w(TAG, "notifyLog: mMainHandler is null");
            return;
        }
        logBuffer.append(msg + "\n");
        Message message = Message.obtain();
        message.what = MainActivity.MSG_LOG;
        message.obj = logBuffer.toString();
        Log.w(TAG, "notifyLog: " + msg);
        mMainHandler.sendMessage(message);

//        Log.i(TAG, "notifyLog: msg"+msg);
    }

    public static void clearLog(){
        if (mMainHandler==null){
            Log.w(TAG, "notifyLog: mMainHandler is null");
            return;
        }
        logBuffer.setLength(0);
        Message message = Message.obtain();
        message.what = MainActivity.MSG_LOG;
        message.obj = logBuffer.toString();
        mMainHandler.sendMessage(message);
    }

}
