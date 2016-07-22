package com.xiaochong.camera;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.xiaochong.camera.util.local.GetMediaImpl;
import com.xiaochong.camera.util.net.LoginImpl;
import com.xiaochong.camera.util.net.UploadImpl;

/**
 * Created by user on 7/18/16.
 */
public class WorkCenter {
    public GetMediaImpl mGetMedia;
    public UploadImpl mUpload;
    public Activity mActivity;
    public LoginImpl mLogin;
    public String mToken;

    public WorkCenter(Activity activity) {
        this.mActivity = activity;
        mGetMedia = new GetMediaImpl(activity, this);
        mUpload = new UploadImpl(activity);
        mLogin = new LoginImpl(activity, this);
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
        if (!TextUtils.isEmpty(mToken)) {
            mUpload.uploadFile(type, path, mToken);
        }
    }

    public void login() {
        mLogin.startLogin("ryan", "123456");
    }

    public void upload() {
    }

    public void setToken(String token) {
        this.mToken = token;
    }
}
