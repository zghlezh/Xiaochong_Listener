package com.xiaochong.camera.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.xiaochong.camera.util.HttpSender;
import com.xiaochong.camera.util.UploadImpl;
import com.xiaochong.camera.util.WorkCenter;

/**
 * Created by Administrator on 2016/6/4.
 */
public class WorkService extends Service{
    private static final String TAG = "WorkService" ;
    public static final String ACTION = "com.xiaochong.WorkService";
    public HttpBinder mBinder;
    public WorkCenter mWorkCenter;
    public HttpSender mSender;

    public void setWorkCenter(WorkCenter workcenter) {
        this.mWorkCenter =  workcenter;
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

    public HttpSender getInstance(WorkCenter workCenter) {
        if (mSender == null) {
            mSender = new HttpSender(workCenter);
        }
        return mSender;
    }

    public void startLogin() {
        if (mWorkCenter != null) {
            Log.i("qinghao", "==============workservice startLogin==============");
            mWorkCenter.login();
        }
    }


    public class HttpBinder extends Binder {
        public WorkService getService() {
            Log.i(TAG, "WorkService get service");
            return WorkService.this;
        }

        public void startConnection() {
            mSender = getInstance(mWorkCenter);
            mSender.sendHttp();
        }
    }
}
