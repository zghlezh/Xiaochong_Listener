package com.xiaochong.camera;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.xiaochong.camera.service.WorkService;
import com.xiaochong.camera.service.WorkService.HttpBinder;
import com.xiaochong.camera.util.WorkCenter;


public class ControlActivity extends Activity {
    public static final String TAG = "ControlActivity";

    private Context mContext;
    private WorkService mWorkService;
    private WorkCenter mWorkCenter;

    public ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected");
            HttpBinder binder = (HttpBinder) service;
            mWorkService = binder.getService();
            mWorkService.setWorkCenter(mWorkCenter);
            binder.startConnection();
//            mWorkService.startLogin();
        }
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.control);
        Log.i(TAG, "init");
        mContext = ControlActivity.this;
        mWorkCenter = new WorkCenter(ControlActivity.this);
        Intent serviceIntent = new Intent(this, WorkService.class);
        bindService(serviceIntent, conn, BIND_AUTO_CREATE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult ");
        mWorkCenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
