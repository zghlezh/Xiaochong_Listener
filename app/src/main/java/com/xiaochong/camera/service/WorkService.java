package com.xiaochong.camera.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.xiaochong.camera.util.IGetMedia;

/**
 * Created by Administrator on 2016/6/4.
 */
public class WorkService extends Service{
    private static final String TAG = "WorkService" ;
    public static final String ACTION = "com.xiaochong.WorkService";
    public HttpBinder mBinder;
    public IGetMedia mGetMedia;

    public void setMediaGet(IGetMedia getMedia) {
        this.mGetMedia =  getMedia;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate invoke");
        super.onCreate();
        mBinder = new HttpBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand invoke and the flag is "+flags+ " and startId is "+startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "onStartCommand");
        super.onStart(intent, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind invoke");
        return mBinder;
    }

    public int ConnectServer() {
        return 0;
    }

    private class uploadFile implements Runnable {

        @Override
        public void run() {

        }
    }

    public class HttpBinder extends Binder {
        public WorkService getService() {
            Log.i(TAG, "WorkService get service");
            return WorkService.this;
        }
    }
}
