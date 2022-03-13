package com.example.socketdemo;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socketdemo.been.BaseParameters;
import com.example.socketdemo.been.BaseRequestBeen;
import com.example.socketdemo.been.DiscoverRequest;
import com.example.socketdemo.been.RtspRequestBeen;
import com.example.socketdemo.been.VolumeRequest;
import com.example.socketdemo.ioSocket.ClientManager;
import com.example.socketdemo.ioSocket.ServerIoSocket;
import com.google.gson.Gson;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    public static String TAG = "MainActivity_SocketServer";
    //    public static final String HOST = "192.168.10.253";
    public static final String HOST = "192.168.10.241";// meeting
//    public static final String HOST = "192.168.10.186";// vivo

    //    private static final String HOST = "127.0.0.1";
    public static final int PORT = 5656;
    public static final String LOCAL_HOST = "/users";

    public static final String Uri = "";

    public static ExecutorService mExecutorService = null;

    private EditText et_input, et_ip;
    private Button bt_sendClientMsg, bt_closeClient, bt_clearLog, btnSpeak;
    private TextView tv_log;

    private String discoverUrl = "";

    public static final int MSG_LOG = 10001;

    private static volatile boolean discovered = false;

    public static Gson gson = new Gson();

    private Handler mMainHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case MSG_LOG:
                    String msg = (String) message.obj;
                    tv_log.setText(msg);
                    break;
                default:
                    break;
            }

            return false;
        }
    });
    private ServerIoSocket server;
//    private OkClient client;
//    private SocketServer server;
//    private SocketClient client;

    private HttpUtils httpUtils = new HttpUtils();
    private HandlerThread handlerThread = new HandlerThread("httpThread");
    private Handler workHandler;
    private TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.setNotifyHandler(mMainHandler);

        tts = new TextToSpeech(this, this);

        //启动apk
//        String packname="com.kandaovr.meeting.meetinghost";
//        PackageManager packageManager = getPackageManager();
//        Intent installIntent = packageManager.getLaunchIntentForPackage(packname);
//        startActivity(installIntent);

        mExecutorService = Executors.newCachedThreadPool();
        handlerThread.start();
        workHandler = new Handler(handlerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                return false;
            }
        });

        et_input = findViewById(R.id.et_input);
        et_input.setText("こんにちは");
        et_ip = findViewById(R.id.et_ip);
        bt_sendClientMsg = findViewById(R.id.bt_sendClientMsg);
        bt_closeClient = findViewById(R.id.bt_closeClient);
        btnSpeak = findViewById(R.id.btnSpeak);
        tv_log = findViewById(R.id.tv_log);
//        client = new SocketClient();
//        server = new SocketServer();
        server = new ServerIoSocket();
//        client = new OkClient();

//      client
        findViewById(R.id.bt_startClient).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = et_ip.getText().toString().trim();
                Log.i(TAG, "onClick: input ip:" + ip);
                ClientManager.getInstance().connect(ip);
            }
        });

        bt_closeClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClientManager.getInstance().close();
            }
        });

        bt_sendClientMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClientManager.getInstance().sendMsg(et_input.getText().toString().trim());
            }
        });

//      server
        findViewById(R.id.bt_startServer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                server.start();
            }
        });


        findViewById(R.id.bt_clearLog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.clearLog();
            }
        });

        findViewById(R.id.bt_stopServer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                server.close();
            }
        });

        findViewById(R.id.bt_broadMsg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                server.broadcast();
            }
        });

        findViewById(R.id.bt_localIP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String localIP = SearchDevicesTest.getLocalWifiIP();
                tv_log.setText("本端局域网IP：" + localIP);
            }
        });

        findViewById(R.id.bt_startApkService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent socketIntent = new Intent();
                    ComponentName cnSocket = new ComponentName("com.kandaovr.meeting.meetinghost",
                            "com.kandaovr.meeting.meetinghost.socketio.SocketServer");
                    socketIntent.setComponent(cnSocket);
                    MainActivity.this.startService(socketIntent);

                    Intent andServerIntent = new Intent();
                    ComponentName cnAnd = new ComponentName("com.kandaovr.meeting.meetinghost",
                            "com.kandaovr.meeting.meetinghost.andserver.AndService");
                    andServerIntent.setComponent(cnAnd);
                    MainActivity.this.startService(andServerIntent);
                } catch (Exception e) {
                    Log.i(TAG, "onClick: error;" + e.getMessage());
                }
            }
        });

        findViewById(R.id.bt_httpDiscover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String localIP = SearchDevicesTest.getLocalWifiIP();
                if (localIP.isEmpty()) {
                    Log.i(TAG, "httpDiscover: localIp is empty");
                    return;
                }
                String ipPort = "";
                try {
                    ipPort = localIP.substring(0, localIP.lastIndexOf("."));
                } catch (Exception e) {
                    Log.e(TAG, "httpDiscover: ipPort sub error", e);
                }
                if (!ipPort.isEmpty()) {
                    discovered = false;
                    DiscoverRequest discoverRequest = new DiscoverRequest();
                    discoverRequest.setName("discover");
                    discoverRequest.setParameters(new Object());
                    String paramJson = gson.toJson(discoverRequest);
                    Log.i(TAG, "onClick: sendPostForJson paramJson" + paramJson);
                    for (int i = 1; i < 255; i++) {
                        // 网段端口探测
                        String getUrl = "http://" + ipPort + "." + i + ":8080/meetingHost";
                        httpDiscover(getUrl, paramJson);
                    }
                }
            }
        });

        findViewById(R.id.bt_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TestActivity.class));
            }
        });
        findViewById(R.id.bt_beep).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBeepPost();
            }
        });
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakOut();
            }
        });
    }


    private void httpDiscover(String httpUrl) {
        workHandler.post(new Runnable() {
            @Override
            public void run() {
                String resJson = httpUtils.sendGetRequest(httpUrl);
                if (!resJson.isEmpty()) {
                    workHandler.removeCallbacksAndMessages(null);
                    Message message = Message.obtain();
                    message.what = MainActivity.MSG_LOG;
                    message.obj = httpUrl;
                    discoverUrl = httpUrl;
                    mMainHandler.sendMessage(message);
                }
                Log.i(TAG, "run: sendGetRequest result:" + resJson);
            }
        });
    }

    private void httpDiscover(String httpUrl, String paramJson) {
        workHandler.post(new Runnable() {
            @Override
            public void run() {
                String resJson = httpUtils.sendPostForJson(httpUrl, paramJson);
                if (!resJson.isEmpty()) {
//                    workHandler.removeCallbacksAndMessages(null);
                    Message message = Message.obtain();
                    message.what = MainActivity.MSG_LOG;
                    message.obj = httpUrl;
                    mMainHandler.sendMessage(message);
                    //探测到，发送闪灯提示http
                    discoverUrl = httpUrl;
                    sendBeepPost();
                }
                Log.i(TAG, "run: sendPostForJson result:" + resJson);
            }
        });
    }

    private void sendBeepPost() {
//        if (discoverUrl.isEmpty()) {
//            Log.i(TAG, "sendBeepPost: discoverUrl is empty");
//            return;
//        }
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: sendBeepPost start ");
                BaseRequestBeen beepRequest = new BaseRequestBeen();
                beepRequest.setName("beep");
                beepRequest.setTime(System.currentTimeMillis());
                beepRequest.setUuid("uuid123");
                String json = gson.toJson(beepRequest);
                String beepResult = httpUtils.sendPostForJson("http://192.168.10.248:8080/meetingHost", json);
                Log.i(TAG, "run: sendBeepPost " + beepResult);
            }
        });

    }

    @Override
    protected void onDestroy() {
        ClientManager.getInstance().close();
        super.onDestroy();
    }

    private int status;

    @Override
    public void onInit(int i) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                btnSpeak.setEnabled(true);
                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void speakOut() {

        String text = et_input.getText().toString();
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
    }

}