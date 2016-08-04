package com.xiaochong.camera.util.net;

import android.util.Log;

import com.xiaochong.camera.WorkCenter;
import com.zebra.test.ConnectHandler;
import com.zebra.test.Sender;

import java.util.logging.Level;

/**
 * Created by user on 7/18/16.
 */
public class TcpSender {
    public static final String TAG = "TcpSender";
    public static Sender sender = Sender.get();
    public WorkCenter mWorkCenter;

    public TcpSender(WorkCenter workCenter) {
        this.mWorkCenter = workCenter;
    }

    public void sendTcp() {
        Log.i(TAG, "start to sendTcp");
        sender.setLoggerLevel(Level.FINEST);
        //sender.setDeviceId("5C7B080B4CA863AD2A42905465897381");
        sender.startWork(new ConnectHandler() {
            @Override
            public void onException(Throwable throwable) {
                Log.i(TAG, "onException");
            }

            @Override
            public void onMessageReceived(byte[] bytes) {
                String order = new String(bytes).trim();
                Log.i("qinghao", "receive the order is "+order);
                int i = Integer.parseInt(order);
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
                sender.login(mWorkCenter.getToken());
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
