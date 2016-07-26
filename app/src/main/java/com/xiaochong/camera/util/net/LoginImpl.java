package com.xiaochong.camera.util.net;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.xiaochong.camera.WorkCenter;

/**
 * Created by user on 7/22/16.
 */
public class LoginImpl implements ILogin {
    private final String TAG = "LoginImpl";
    private Activity mActivity;
    private WorkCenter mWorkCenter;

    public LoginImpl(Activity mActivity, WorkCenter work) {
        this.mActivity = mActivity;
        mWorkCenter = work;
    }

    @Override
    public void startLogin(String act, String pwd) {
        Log.i(TAG, "===============start to login =====================");
        RequestParams params = new RequestParams();
        try {
            params.put("userName", "ryan");
            params.put("passwd", "123456");
            AsyncHttpClient client = new AsyncHttpClient();
            client.post("http://115.28.202.110:8888/login", params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                    String bytesString = new String(bytes);
                    Log.i(TAG, "the httpcode is "+i +" and the header is "+
                            headers.toString()+" and the bytes is "+bytesString);
                    Toast.makeText(mActivity, "login success", Toast.LENGTH_SHORT).show();
                    JSONObject obj = JSONObject.parseObject(bytesString);
                    JSONObject data = obj.getJSONObject("data");
                    final String token = data.getString("token");
                    Log.i(TAG, " startLogin token is  ===================:"+token);
                    mWorkCenter.setToken(token);
                    mWorkCenter.startTcp();
                }

                @Override
                public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                    String failString =  new String(bytes);
                    if (headers != null){
                        Log.i(TAG, "login on Failure header:"+ headers.toString());
                    } else {
                        Log.i(TAG, " headers is null ===================:");
                    }
                    if (bytes !=null) {
                        Log.i(TAG, " byte string is "+failString);
                    } else {
                        Log.i(TAG, " byte is null ===================:");
                    }
                    Toast.makeText(mActivity, "login failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
