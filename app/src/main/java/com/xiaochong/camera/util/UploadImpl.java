package com.xiaochong.camera.util;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by user on 6/8/16.
 */
public class UploadImpl {
    public final String TAG = "UploadImpl";
    public final String DCIM =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();

    public final String SDCARD =
            Environment.getExternalStorageDirectory().toString();

    public Activity mActivity;

    public UploadImpl(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public String getFilePath() {
        String path = getSDPath();
        if (TextUtils.isEmpty(path)) {
            path = mActivity.getExternalFilesDir(null).toString();
        }
        Log.i("aaa", "the path is "+path);
        path = path + "/cameralisten.jpg";
        return path;
    }

    public String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if(sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

//    public void uploadFile(Context mContext) {
//        String path = getFilePath(mContext);
//        File myFile = new File(path);
//        RequestParams params = new RequestParams();
//        try {
//            InputStream instream = new FileInputStream(myFile);
//            params.put("token", "f26bcdee-5293-4d8b-ad58-f6d2879dc1f8");
//            //params.put("file", myFile);
//            params.put("file", instream);
//            AsyncHttpClient client = new AsyncHttpClient();
//            client.post("http://115.28.202.110:8888/user/upload", params, new AsyncHttpResponseHandler() {
//
//                @Override
//                public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
//                    Log.i(TAG, "the httpcode is "+i +" and the header is "+
//                            headers.toString()+" and the bytes is "+bytes.toString());
//                }
//
//                @Override
//                public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
//                    Log.i(TAG, "upload on Failure");
//                }
//            });
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//    }


    public void uploadFile(int type, String path) {
        Log.i(TAG, "===============start to upload type:"+ type +"=================");
        File myFile = new File(path);
        RequestParams params = new RequestParams();
        try {
            InputStream instream = new FileInputStream(myFile);
            params.put("token", "f26bcdee-5293-4d8b-ad58-f6d2879dc1f8");
            //params.put("file", myFile);
            params.put("file", instream);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post("http://115.28.202.110:8888/user/upload", params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                    Log.i(TAG, "the httpcode is "+i +" and the header is "+
                            headers.toString()+" and the bytes is "+bytes.toString());
                    Toast.makeText(mActivity, "upload success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.i(TAG, "upload on Failure");
                    Toast.makeText(mActivity, "upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
