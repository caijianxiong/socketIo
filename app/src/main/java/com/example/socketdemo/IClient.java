package com.example.socketdemo;

public interface IClient {
     void connect(String ip);
     void sendMsg(String msg);
     void onReceiveServerMsg(String msg);
     void close();

}
