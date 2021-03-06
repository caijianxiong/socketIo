package com.example.socketdemo.ioSocket;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.example.socketdemo.IServer;
import com.example.socketdemo.LogUtil;
import com.example.socketdemo.MainActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerIoSocket implements IServer {


    private SocketIOServer server;
    private Map<UUID, SocketIOClient> clientMap = new HashMap<>();

    @Override
    public void start() {
        MainActivity.mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                startServer();
            }
        });
    }

    @Override
    public void close() {
        server.removeAllListeners("client_info");
        server.stop();

    }

    @Override
    public void broadcast() {
        MainActivity.mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                server.getBroadcastOperations().sendEvent("serverBroadcast","this is server broadcast msg");
            }
        });

    }


    private void startServer(){
        Configuration config = new Configuration();
//        config.setHostname(MainActivity.HOST);
        config.setPort(MainActivity.PORT);
        // todo json
//        config.setJsonSupport();
        // todo authorization
        config.setAuthorizationListener(new AuthorizationListener() {
            @Override
            public boolean isAuthorized(HandshakeData data) {
                // todo ?????????????????????????????????true????????????
                LogUtil.notifyLog("server isAuthorized :");
//                if (clientMap.size() > 0) {
//                    // ??????
//                    return false;
//                }
                return true;
            }
        });
        // ssl ??????
//        config.setSSLProtocol();
        final SocketConfig socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);
        config.setSocketConfig(socketConfig);
        server = new SocketIOServer(config);
        LogUtil.notifyLog("server start");
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                if (client==null){
                    LogUtil.notifyLog("server rev client is null");
                }else {

                    client.sendEvent("helloPush", "hello");
                    if (clientMap.size() > 0) {
                        // ?????? 1, ?????? 2????????????
                        client.sendEvent("kickOut", "reason server just support one client");
                        client.disconnect();
                        LogUtil.notifyLog("server kickOut client");
                    } else {
                        UUID uuid = client.getSessionId();
                        clientMap.put(uuid, client);
                        LogUtil.notifyLog("server client connect success");
                    }

                }
            }
        });

        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                LogUtil.notifyLog("server rev client onDisconnect");
                clientMap.remove(client.getSessionId());
            }
        });

        server.addEventListener("client_info", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackSender) throws Exception {
                LogUtil.notifyLog("server rev client_info :"+data);
            }
        });

        server.start();
        LogUtil.notifyLog("server started");

//        while (true){
//            try {
//                Thread.sleep(1500);
//                //????????????
//                server.getBroadcastOperations().sendEvent("borcast","are you live?");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

//    @OnEvent("client_info")
//    public void onClientMessage(SocketIOClient client, String data){
//        LogUtil.notifyLog("server rev client :"+client.getSessionId()+"-----Message :"+data);
//    }


}
