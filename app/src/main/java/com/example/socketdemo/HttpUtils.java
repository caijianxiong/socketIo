package com.example.socketdemo;

import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class HttpUtils {

    private OkHttpClient httpClient;
    private static final String TAG = "HttpUtils";

    public HttpUtils() {
        httpClient=new OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .connectTimeout(400, TimeUnit.MILLISECONDS)
                .build();
    }

    public String sendGetRequest(String url) {

        // 同步请求
        Log.i(TAG, "sendGetRequest: start url:" + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = httpClient.newCall(request);
        try {
            Response response = call.execute();
            String res = "";
            ResponseBody responseBody = response.body();
            long contentLength = responseBody.contentLength();
            if (!bodyEncoded(response.headers())) {
                BufferedSource source = responseBody.source();
                try {
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                } catch (IOException e) {
                }
                Buffer buffer = source.buffer();

                Charset charset = Charset.forName("UTF-8");
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(Charset.forName("UTF-8"));
                    } catch (UnsupportedCharsetException e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }
                if (contentLength != 0) {
                    res = buffer.clone().readString(charset);
                }
            }
            return res;
        } catch (IOException e) {
            Log.e(TAG, "sendGetRequest: error"+e.getMessage());
        }
        return "";
    }

    public String sendPostForJson(String url,String json) {

        // 同步请求
        Log.i(TAG, "sendPostForJson: start url:" + url);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = httpClient.newCall(request);
        try {
            Response response = call.execute();
            String res = "";
            ResponseBody responseBody = response.body();
            long contentLength = responseBody.contentLength();
            if (!bodyEncoded(response.headers())) {
                BufferedSource source = responseBody.source();
                try {
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                } catch (IOException e) {
                }
                Buffer buffer = source.buffer();

                Charset charset = Charset.forName("UTF-8");
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(Charset.forName("UTF-8"));
                    } catch (UnsupportedCharsetException e) {
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }
                if (contentLength != 0) {
                    res = buffer.clone().readString(charset);
                }
            }
            return res;
        } catch (IOException e) {
            Log.e(TAG, "sendPostForJson: error"+e.getMessage());
        }
        return "";
    }



    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }
}
