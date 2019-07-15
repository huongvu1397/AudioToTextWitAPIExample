package com.example.audiototextwitapiexample.utils;

import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

public class FFmpegExeResponseHandler implements FFmpegExecuteResponseHandler {
    private volatile boolean finish = false;
    private String message;

    public void onSuccess(String message) {
        this.message = message;
        System.out.println("CONVERSION: " + message);
    }

    public void onProgress(String message) {
    }

    public void onFailure(String message) {
        this.message = message;
        System.out.println("CONVERSION: " + message);
    }

    public void onStart() {
    }

    public void onFinish() {
        this.finish = true;
        System.out.println("CONVERSION FINISHED");
    }

    public boolean isFinish() {
        return this.finish;
    }

    public String getMessage() {
        return this.message;
    }
}