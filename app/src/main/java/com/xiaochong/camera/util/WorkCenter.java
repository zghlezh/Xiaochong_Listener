package com.xiaochong.camera.util;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by user on 7/18/16.
 */
public class WorkCenter {
    public GetMediaImpl mGetMedia;
    public UploadImpl mUpload;
    public Activity mActivity;

    public WorkCenter(Activity activity) {
        this.mActivity = activity;
        mGetMedia = new GetMediaImpl(activity, this);
        mUpload = new UploadImpl(activity);
    }

    //getMedia

    public void getPhoto() {
        mGetMedia.getPhoto();
    }

    public void getVideo() {
        mGetMedia.getVideo();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mGetMedia.onActivityResult(requestCode, resultCode, data);
    }

    //upload
    public void uploadFile(int type, String path) {
        mUpload.uploadFile(type, path);
    }

    public void login() {
        mUpload.login();
    }
}
