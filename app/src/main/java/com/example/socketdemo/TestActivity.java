package com.example.socketdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;

import com.example.socketdemo.been.VolumeRequest;
import com.example.socketdemo.ioSocket.ClientManager;
import com.google.gson.Gson;

public class TestActivity extends AppCompatActivity {

    private static String TAG = "TestActivity";
    private SeekBar seekBar;
    private AudioManager mAudioManager;
    private int progress;
    private int systemProgress;
    private int audioMaxSystemVolume;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        findViewById(R.id.bt_volumeAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add
                addVolume();
            }
        });
        findViewById(R.id.bt_volumeLess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // less
                lessVolume();
            }
        });

        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        Log.i(TAG, "onProgressChanged: i:" + i + "--b:" + b);
//                        int systemVolume = (int) Math.floor(audioMaxSystemVolume / 100.00 * i);
//                        if (systemVolume == systemProgress) {
//                            Log.i(TAG, "lessVolume: same volume " + "systemVolume :" + systemVolume + " systemProgress:" + systemProgress);
//                            return;
//                        }
//                        progress = i;
//                        systemProgress = systemVolume;
//                        Log.i(TAG, "lessVolume: " + systemVolume);
//                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, systemProgress, AudioManager.FLAG_SHOW_UI);
//
                        VolumeRequest volumeRequest = new VolumeRequest();
                        volumeRequest.setName(ConstantConfig.VOLUME_ORDER_KEY);
                        volumeRequest.setParameters(new VolumeRequest.Parameters(i));
                        String json=gson.toJson(volumeRequest);
                        ClientManager.getInstance().sendMsg(json);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );


        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioMaxSystemVolume = mAudioManager.getStreamMaxVolume(mAudioManager.STREAM_MUSIC);
        systemProgress = mAudioManager.getStreamVolume(mAudioManager.STREAM_MUSIC);
        progress = (int) Math.ceil(100.00 * systemProgress / audioMaxSystemVolume);

        Log.i(TAG, "onCreate: audioMaxVolume:" + audioMaxSystemVolume);
        seekBar.setMax(100);
//        seekBar.setProgress(progress);

    }

    //手机音量+-按钮监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                lessVolume();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                addVolume();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void addVolume() {
        if (progress == 100) {
            return;
        }
        ++progress;
        seekBar.setProgress(progress);
    }


    private void lessVolume() {
        if (progress == 0) {
            return;
        }
        --progress;
        seekBar.setProgress(progress);
    }


}