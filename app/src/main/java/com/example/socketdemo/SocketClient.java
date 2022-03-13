package com.example.socketdemo;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class SocketClient implements IClient{

    private String TAG = this.getClass().getSimpleName();
    private String receiveMsg;

    private Socket mSocket;
    private BufferedReader br;
    private OutputStream outputStream;

    @Override
    public void connect(String ip) {
        Thread thread= new Thread(new connectRunnable());
        thread.start();
    }

    @Override
    public void sendMsg(String msg) {
        if (mSocket==null){
            LogUtil.notifyLog("socket is null");
            return;
        }

        if (!mSocket.isConnected()){
            LogUtil.notifyLog("socket not connect");
            return;
        }

        if (msg.isEmpty()){
            LogUtil.notifyLog("send msg is empty");
        }
        MainActivity.mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                if (mSocket != null && mSocket.isConnected()) {
                    try {
                        // 步骤1：从Socket 获得输出流对象OutputStream 该对象作用：发送数据
                        outputStream = mSocket.getOutputStream();
                        // 步骤2：写入需要发送的数据到输出流对象中 ,,,特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞
                        outputStream.write((msg+"\n").getBytes("utf-8"));
                        // 步骤3：发送数据到服务端
                        outputStream.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                        LogUtil.notifyLog("client send error:"+e.getMessage());
                    }
                }
            }
        });

    }

    @Override
    public void onReceiveServerMsg(String msg) {

    }

    @Override
    public void close() {

        try {
            // 断开 客户端发送到服务器 的连接，即关闭输出流对象OutputStream
            if (outputStream != null) {
                outputStream.close();
            }
            // 断开 服务器发送到客户端 的连接，即关闭输入流读取器对象BufferedReader
            if (br != null) {
                br.close();
            }
            if (mSocket != null) {
                // 最终关闭整个Socket连接
                mSocket.close();
                LogUtil.notifyLog("client is connect to server:" + mSocket.isConnected());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private class connectRunnable implements Runnable {

        @Override
        public void run() {//可以考虑在此处添加一个while循环，结合下面的catch语句，实现Socket对象获取失败后的超时重连，直到成功建立Socket连接
            try {
                LogUtil.notifyLog("create socket start");
                mSocket = new Socket(MainActivity.HOST, MainActivity.PORT);      //步骤一
                mSocket.setSoTimeout(10000);

                LogUtil.notifyLog("create socket end");
                // 步骤1：创建输入流对象InputStream
                InputStream is = mSocket.getInputStream();

                // 步骤2：创建输入流读取器对象 并传入输入流对象,该对象作用：获取服务器返回的数据
                InputStreamReader isr = new InputStreamReader(is,"UTF-8");
//                br = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "UTF-8"));
                br = new BufferedReader(isr);
                ClientReceiveMsg();
            } catch (Exception e) {
                LogUtil.notifyLog("create socket failed:" + e.getMessage());
                Log.e(TAG, ("connectService:" + e.getMessage()));   //如果Socket对象获取失败，即连接建立失败，会走到这段逻辑
            }
        }

        private void ClientReceiveMsg() {
            try {
                while (true) {                                      //步骤三
                    if ((receiveMsg = br.readLine()) != null) {
                        LogUtil.notifyLog("client receive  server msg :"+receiveMsg);
                    }
                }
            } catch (IOException e) {
                LogUtil.notifyLog("client receiveMsg Error: "+e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
