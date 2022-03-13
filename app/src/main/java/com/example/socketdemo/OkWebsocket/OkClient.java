package com.example.socketdemo.OkWebsocket;

import com.example.socketdemo.IClient;
import com.example.socketdemo.LogUtil;
import com.example.socketdemo.MainActivity;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class OkClient implements IClient {
    @Override
    public void connect(String ip) {
        OkHttpClient mClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(3, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(3, TimeUnit.SECONDS)//设置连接超时时间
                .retryOnConnectionFailure(false)
                .build();

        //连接地址
        String url = "ws://"+ MainActivity.HOST+":"+MainActivity.PORT;
//构建一个连接请求对象
        Request request = new Request.Builder().header("Connection","close")
        .addHeader("Accept-Encoding", "").get().url(url).build();//ccept-Encoding




        WebSocket mWebSocket=mClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                LogUtil.notifyLog("client onOpen");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                LogUtil.notifyLog("client onFailure"+t.getCause().getMessage());
            }
        });
    }

    @Override
    public void sendMsg(String msg) {

    }

    @Override
    public void onReceiveServerMsg(String msg) {

    }

    @Override
    public void close() {

    }
}
