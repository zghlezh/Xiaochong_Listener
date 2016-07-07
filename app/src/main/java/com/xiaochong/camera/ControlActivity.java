package com.xiaochong.camera;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xiaochong.camera.service.WorkService;
import com.xiaochong.camera.service.WorkService.HttpBinder;
import com.xiaochong.camera.util.IGetMedia;
import com.xiaochong.camera.util.UploadUtil;


public class ControlActivity extends Activity implements IGetMedia {
    public static final String TAG = "ControlActivity";
    public static final String START_ACTION = "start_action";
    public static final String GET_PHOTO = "get_photo";
    public static final String GET_VIDEO = "get_video";
    public static final String GET_VIDEO_LENGTH = "get_video_length";
    public static final String GET_PHOTO_PATH = "get_photo_path";
    public static final String GET_VIDEO_PATH = "get_video_path";
    public static final int KEY_GET_PHOTO = 1;
    public static final int KEY_GET_VIDEO = 2;

    public static final int RECORD_TIME = 5000;

    private Context mContext;
    private WorkService mWorkService;

    private Button mGetPhoto;
    private Button mGetVideo;

    public ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected");
            HttpBinder binder = (HttpBinder) service;
            mWorkService = binder.getService();
            mWorkService.setMediaGet(ControlActivity.this);
        }
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control);
        init();
    }

    private void init() {
        mContext = ControlActivity.this;
        mGetPhoto = (Button) findViewById(R.id.get_photo);
        mGetVideo = (Button) findViewById(R.id.get_video);
        mGetPhoto.setOnClickListener(clickListener);
        mGetVideo.setOnClickListener(clickListener);
        Intent serviceIntent = new Intent();
        serviceIntent.setAction(WorkService.ACTION);
        serviceIntent.setPackage("com.xiaochong.camera.service.WorkService");
        bindService(serviceIntent, conn, BIND_AUTO_CREATE);
    }

    View.OnClickListener clickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.get_photo) {
                startToTakePhoto();
            } else {
                startToRecord();
            }
        }
    };

    private void startToTakePhoto() {
        Intent photoIntent = new Intent(mContext, CameraActivity.class);
        photoIntent.putExtra(START_ACTION, GET_PHOTO);
        ControlActivity.this.startActivityForResult(photoIntent, KEY_GET_PHOTO);
    }

    private void startToRecord() {
        Log.i(TAG, "startToRecord");
        Intent videoIntent = new Intent(mContext, CameraActivity.class);
        videoIntent.putExtra(START_ACTION, GET_VIDEO);
        videoIntent.putExtra(GET_VIDEO_LENGTH, RECORD_TIME);
        ControlActivity.this.startActivityForResult(videoIntent, KEY_GET_VIDEO);
    }

    @Override
    public String getPhoto() {

        return null;
    }

    @Override
    public String getVideo() {
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult ");
        if (data == null) {
            Log.i(TAG, "onActivityResult and data is null");
            return;
        }
        switch (resultCode) {
            case KEY_GET_PHOTO:
                String photoPath = data.getStringExtra(GET_PHOTO_PATH);
                Log.i(TAG, "get the photo Path:  "+photoPath+ " and start to upload");
                Toast.makeText(mContext, "onActivityResult is invoke and start to upload file:"+photoPath, Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(photoPath)) {
                    UploadUtil.uploadFile(ControlActivity.this, KEY_GET_PHOTO, photoPath);
                }
                break;
            case KEY_GET_VIDEO:
                String videoPath = data.getStringExtra(GET_VIDEO_PATH);
                Log.i(TAG, "get the videoPath Path:  "+videoPath+ " and start to upload");
                Toast.makeText(mContext, "onActivityResult is invoke and start to upload file:"+videoPath, Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(videoPath)) {
                    UploadUtil.uploadFile(ControlActivity.this, KEY_GET_VIDEO, videoPath);
                }
                break;
            default:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
