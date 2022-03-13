package com.example.socketdemo.ioSocket;

import com.example.socketdemo.IClient;
import com.example.socketdemo.LogUtil;
import com.example.socketdemo.MainActivity;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.EngineIOException;
import io.socket.engineio.client.Transport;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.OkHttpClient;

public class ClientIoSocket implements IClient {

    final TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};

                }
            }
    };

    private Socket socket;

    @Override
    public void connect(String ip) {
        MainActivity.mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
//                String wsUrl="ws://"+MainActivity.HOST+":"+MainActivity.PORT;
//                LogUtil.notifyLog("client connect wsUrl:"+wsUrl);
                connectByIp(ip);
            }
        });
    }

    @Override
    public void sendMsg(String msg) {
        MainActivity.mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                send(msg);
            }
        });
    }

    @Override
    public void onReceiveServerMsg(String msg) {

    }

    @Override
    public void close() {
        MainActivity.mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                closeSocket();
            }
        });
    }

    private void closeSocket() {
        socket.disconnect();
        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(Socket.EVENT_CONNECTING, onConnecting);
        socket.off(Socket.EVENT_RECONNECT, onReconnectListener);
        socket.off("serverBroadcast", onServerMsg);
        socket.off("kickOut", onKickOutListener);
        socket.close();
    }

    private void connectByIp(String ip) {
        try {


            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .build();
            IO.setDefaultOkHttpCallFactory(okHttpClient);
            IO.setDefaultOkHttpWebSocketFactory(okHttpClient);

            String host = ip.isEmpty() ? MainActivity.HOST : ip;
            // 强制使用http
            String uri = "http://" + host + ":" + MainActivity.PORT;
            LogUtil.notifyLog("client connect uri:" + uri);
            IO.Options options = new IO.Options();
            // 非常重要，使用webSocket类型 实现长连接，，不设置，会使用http 导致报错
            options.transports = new String[]{WebSocket.NAME};
            options.reconnection = true;
//            options.reconnectionAttempts = 3;
            options.reconnectionDelay = 5000;
            options.timeout = 10000;

            socket = IO.socket(uri, options);
            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.on(Socket.EVENT_CONNECTING, onConnecting);
            socket.on(Socket.EVENT_RECONNECT, onReconnectListener);
            socket.on("serverBroadcast", onServerMsg);
            socket.on("kickOut", onKickOutListener);
            socket.on("helloPush", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    LogUtil.notifyLog("client rev server:" + args[0]);
                }
            });
            socket.on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    LogUtil.notifyLog("client EVENT_TRANSPORT headers");

                    Transport transport = (Transport) args[0];
                    transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Map<String, List<String>> headers = (Map<String, List<String>>) args[0];
                            if (headers != null)
                                LogUtil.notifyLog("client EVENT_TRANSPORT headers:" + headers.size());

                        }
                    });
                }
            });
            socket.connect();
            //循环发送数据
//            while (true) {
//                socket.emit("client_info", " 客户端在发送数据");
//                Thread.sleep(2000);
//            }

        } catch (URISyntaxException e) {
            LogUtil.notifyLog("client on error01:" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void send(String msg) {
        if (socket == null) {
            LogUtil.notifyLog("socket is null");
            return;
        }
//        socket.send(msg);
        socket.emit("client_info", msg);
    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            LogUtil.notifyLog("client  onConnect");
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            LogUtil.notifyLog("client  onDisconnect");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            for (Object error : args) {
                Exception exception = (Exception) error;
                if (exception.getCause() != null) {
                    LogUtil.notifyLog("client  onConnectError:" + exception.getClass().getSimpleName()
                            + "----" + exception.getCause().getMessage());
                }
            }

        }
    };

    private Emitter.Listener onServerMsg = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            String msg = (String) args[0];
            LogUtil.notifyLog(msg);
        }
    };

    private Emitter.Listener onConnecting = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            LogUtil.notifyLog("client  onConnecting");
        }
    };

    private Emitter.Listener onReconnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            LogUtil.notifyLog("client onReconnectListener " + args.toString());
        }
    };

    private Emitter.Listener onKickOutListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if (args.length <= 0) {
                LogUtil.notifyLog("client onKickOutListener ");
                return;
            }
            for (Object msg : args) {
                if (msg instanceof String) {
                    LogUtil.notifyLog("client onKickOutListener reason:" + msg);
                } else {
                    LogUtil.notifyLog("client onKickOutListener " + msg.toString());
                }
            }
        }
    };

}
