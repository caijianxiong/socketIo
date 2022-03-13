package com.example.socketdemo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class SocketServer implements IServer {

    private ServerSocket server = null;
    private List<Socket> mList = new ArrayList<Socket>();

    @Override
    public void start() {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                startServer();
            }
        });
        thread.setName("server");
        thread.start();
    }

    @Override
    public void close() {

    }

    @Override
    public void broadcast() {

    }

    private  void startServer() {
        try {
            server = new ServerSocket(MainActivity.PORT);                         //步骤一
            MainActivity.mExecutorService = Executors.newCachedThreadPool();
            LogUtil.notifyLog("server startServer: 服务器已启动");
            Socket client = null;
            while (true) {
                LogUtil.notifyLog("server accept start");
                client = server.accept();         //步骤二，每接受到一个新Socket连接请求，就会新建一个Thread去处理与其之间的通信
                LogUtil.notifyLog( "server accept one client");
                mList.add(client);
                MainActivity.mExecutorService.execute(new Service(client));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.notifyLog("server startServer error :"+e.getMessage());
        }
    }

    class Service implements Runnable {
        private Socket socket;
        private BufferedReader in = null;
        private PrintWriter printWriter = null;
        private  String receiveMsg;
        private  String sendMsg;

        public Service(Socket socket) {                         //这段代码对应步骤三
            this.socket = socket;
            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream(), "UTF-8"));
                printWriter.println("成功连接服务器" + "（服务器发送）");
                LogUtil.notifyLog("server rev client connect success");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            try {
                while (true) {                                   //循环接收、读取 Client 端发送过来的信息
                    if ((receiveMsg = in.readLine()) != null) {
                        System.out.println("receiveMsg:" + receiveMsg);
                        LogUtil.notifyLog("server receive client msg :" + receiveMsg);
                        if (receiveMsg.equals("0")) {
                            System.out.println("客户端请求断开连接");
                            printWriter.println("服务端断开连接" + "（服务器发送）");
                            mList.remove(socket);
                            in.close();
                            socket.close();                         //接受 Client 端的断开连接请求，并关闭 Socket 连接
                            break;
                        } else {
                            sendMsg = "我已接收：" + receiveMsg + "（服务器发送）";
                            printWriter.println(sendMsg);           //向 Client 端反馈、发送信息
                        }
                    }
                }
            } catch (Exception e) {
                LogUtil.notifyLog("server rev err:"+e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
