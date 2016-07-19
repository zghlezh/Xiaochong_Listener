package com.xiaochong.camera.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.xiaochong.camera.CameraActivity;

/**
 * Created by Administrator on 2016/6/14.
 */
public class GetMediaImpl implements IGetMedia {
    public static final String TAG = "GetMediaImpl";

    public static final String START_ACTION = "start_action";
    public static final String GET_PHOTO = "get_photo";
    public static final String GET_VIDEO = "get_video";
    public static final String GET_VIDEO_LENGTH = "get_video_length";
    public static final String GET_PHOTO_PATH = "get_photo_path";
    public static final String GET_VIDEO_PATH = "get_video_path";
    public static final int KEY_GET_PHOTO = 1;
    public static final int KEY_GET_VIDEO = 2;

    public static final int RECORD_TIME = 5000;

    private Activity mActivity;
    private WorkCenter mWorkCenter;

    public GetMediaImpl(Activity activity, WorkCenter workCenter) {
        this.mActivity = activity;
        mWorkCenter = workCenter;
    }

    @Override
    public void getPhoto() {
        Intent photoIntent = new Intent(mActivity, CameraActivity.class);
        photoIntent.putExtra(START_ACTION, GET_PHOTO);
        mActivity.startActivityForResult(photoIntent, KEY_GET_PHOTO);
    }

    @Override
    public void getVideo() {
        Log.i(TAG, "startToRecord");
        Intent videoIntent = new Intent(mActivity, CameraActivity.class);
        videoIntent.putExtra(START_ACTION, GET_VIDEO);
        videoIntent.putExtra(GET_VIDEO_LENGTH, RECORD_TIME);
        mActivity.startActivityForResult(videoIntent, KEY_GET_VIDEO);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            Log.i(TAG, "onActivityResult and data is null");
            return;
        }
        switch (resultCode) {
            case KEY_GET_PHOTO:
                String photoPath = data.getStringExtra(GET_PHOTO_PATH);
                Log.i(TAG, "get the photo Path:  "+photoPath+ " and start to upload");
                Toast.makeText(mActivity, "onActivityResult is invoke and start to upload file:"+photoPath, Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(photoPath)) {
                    mWorkCenter.uploadFile(KEY_GET_PHOTO, photoPath);
                }
                break;
            case KEY_GET_VIDEO:
                String videoPath = data.getStringExtra(GET_VIDEO_PATH);
                Log.i(TAG, "get the videoPath Path:  "+videoPath+ " and start to upload");
                Toast.makeText(mActivity, "onActivityResult is invoke and start to upload file:"+videoPath, Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(videoPath)) {
                    mWorkCenter.uploadFile(KEY_GET_VIDEO, videoPath);
                }
                break;
            default:

                break;
        }
    }

}
