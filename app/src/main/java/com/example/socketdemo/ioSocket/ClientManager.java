package com.example.socketdemo.ioSocket;

import com.example.socketdemo.IClient;

public class ClientManager implements IClient {

    private static Object object = new Object();
    private ClientIoSocket client;
    private static ClientIoSocket instance;

    private ClientManager() {
        client = new ClientIoSocket();
    }


    public static ClientIoSocket getInstance() {
        if (instance == null) {
            synchronized (object) {
                if (instance == null) {
                    instance = new ClientIoSocket();
                }
            }
        }
        return instance;
    }

    @Override
    public void connect(String ip) {
        client.connect(ip);
    }

    @Override
    public void sendMsg(String msg) {
        client.sendMsg(msg);
    }

    @Override
    public void onReceiveServerMsg(String msg) {

    }

    @Override
    public void close() {
        client.close();
    }




}
