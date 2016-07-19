package com.xiaochong.camera.util;

import android.util.Log;

import com.zebra.test.ConnectHandler;
import com.zebra.test.Sender;

import java.util.logging.Level;

/**
 * Created by user on 7/18/16.
 */
public class HttpSender {
    public static final String TAG = "HttpSender";
    public WorkCenter mWorkCenter;

    public HttpSender(WorkCenter workCenter) {
        this.mWorkCenter = workCenter;
    }

    public void sendHttp() {
        Log.i(TAG, "start to sendHttp");
        Sender sender = Sender.get();
        sender.setLoggerLevel(Level.FINEST);
        sender.setDeviceId("5C7B080B4CA863AD2A42905465897381");
        sender.startWork(1016, "5C7B080B4CA863AD2A42905465897381", new ConnectHandler() {
            @Override
            public void onException(Throwable throwable) {
                Log.i(TAG, "onException");
            }

            @Override
            public void onMessageReceived(int i, byte[] bytes) {
                Log.i("qinghao", "receive the i is "+i+" and the bytes is "+bytes.toString());
                System.out.println("receive the i is "+i+" and the bytes is "+bytes.toString());
                if (i == 1){
                    mWorkCenter.getPhoto();
                } else if (i == 2) {
                    mWorkCenter.getVideo();
                }

            }

            @Override
            public void onMessageCallback(long l, int i, String s) {

            }

            @Override
            public void onConnected() {
                System.out.println("连接成功");
            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onInitialize() {
                System.out.println("初始化成功");
            }

            @Override
            public void onUnInitialize() {
                System.out.println("初始化失败");
            }
        });
    }
}
