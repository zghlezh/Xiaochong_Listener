package com.xiaochong.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.xiaochong.camera.module.CameraModule;
import com.xiaochong.camera.module.PhotoModule;
import com.xiaochong.camera.module.VideoModule;
import com.xiaochong.camera.util.CameraUtil;
import com.xiaochong.camera.util.GetMediaImpl;

public class CameraActivity extends Activity implements
        View.OnClickListener{

    public static final String TAG = "CAM_Activity";
    public static final int PHOTO_MODULE_INDEX = 0;
    public static final int VIDEO_MODULE_INDEX = 1;
    public static final int RECORD_DEFAULT_LENGTH = 4000;

    public Context mContext;
    private int mCurrentModuleIndex;
    private View mCameraModuleRootView;
    private CameraModule mCurrentModule;
    public boolean mIsModuleSwitchInProgress = false;
    private ImageView mSwitcher;
    private PowerManager.WakeLock mWakeLock;
    private String mType;
    private int mRecordLength = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup);
        initType();
        initView();
    }

    @Override
    protected void onResume() {
        mCurrentModule.onResumeBeforeSuper();
        super.onResume();
        mWakeLock.acquire();
        mCurrentModule.onResumeAfterSuper();
        startWork();
    }

    @Override
    protected void onPause() {
        mCurrentModule.onPauseBeforeSuper();
        super.onPause();
        mWakeLock.release();
        mCurrentModule.onPauseAfterSuper();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initView() {
        mSwitcher = (ImageView) findViewById(R.id.camera_switcher);
        mSwitcher.setOnClickListener(CameraActivity.this);
        mContext = CameraActivity.this;
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
    }

    private void initType() {
        Intent intent = getIntent();
        mType = intent.getStringExtra(GetMediaImpl.START_ACTION);
        if (TextUtils.equals(mType, GetMediaImpl.GET_PHOTO)) {
            mCurrentModuleIndex = PHOTO_MODULE_INDEX;
        } else if (TextUtils.equals(mType, GetMediaImpl.GET_VIDEO)) {
            mRecordLength = intent.getIntExtra(GetMediaImpl.GET_VIDEO_LENGTH, RECORD_DEFAULT_LENGTH);
            mCurrentModuleIndex = VIDEO_MODULE_INDEX;
        }
        setModuleFromIndex(mCurrentModuleIndex);
        mCameraModuleRootView = findViewById(R.id.camera_app_root);
        mCurrentModule.init(this, mCameraModuleRootView);
    }

    public int getRecordLength() {
        return mRecordLength;
    }

    private void startWork() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_switcher:
                if (mIsModuleSwitchInProgress == true) {
                    return;
                }
                Log.i(TAG, "camera_switcher click");
                updateModule();
                onModuleSwitch(mCurrentModuleIndex);
                break;
        }
    }

    private void updateModule() {
        if (mCurrentModuleIndex == PHOTO_MODULE_INDEX) {
            mCurrentModuleIndex = VIDEO_MODULE_INDEX;
        } else {
            mCurrentModuleIndex = PHOTO_MODULE_INDEX;
        }
    }


    public void hideSwitcher() {
        mSwitcher.setVisibility(View.INVISIBLE);
    }

    public void showSwitcher() {
        mSwitcher.setVisibility(View.VISIBLE);
    }


    private void setModuleFromIndex(int moduleIndex) {
        mCurrentModuleIndex = moduleIndex;
        switch (moduleIndex) {
            case PHOTO_MODULE_INDEX:
                mCurrentModule = new PhotoModule();
                mCurrentModuleIndex = PHOTO_MODULE_INDEX;
                break;
            case VIDEO_MODULE_INDEX:
                mCurrentModule = new VideoModule();
                mCurrentModuleIndex = VIDEO_MODULE_INDEX;
                break;

            default:
                // Fall back to photo mode.
                mCurrentModule = new PhotoModule();
                mCurrentModuleIndex = PHOTO_MODULE_INDEX;
                //mCurrentModuleIndex = ModuleSwitcher.PHOTO_MODULE_INDEX;
                break;
        }
    }

    public void onModuleSwitch(int moduleIndex) {
        Log.i(TAG, "onModuleSwitch the module index is "+moduleIndex);
        mIsModuleSwitchInProgress = true;
        closeModule(mCurrentModule);
        setModuleFromIndex(moduleIndex);
        openModule(mCurrentModule);
        int imageR = mCurrentModuleIndex == PHOTO_MODULE_INDEX ?
                R.drawable.ic_switch_camera : R.drawable.ic_switch_video;
        mSwitcher.setImageResource(imageR);
        mIsModuleSwitchInProgress = false;
    }


    private void openModule(CameraModule module) {
        module.init(this, mCameraModuleRootView);
        module.onResumeBeforeSuper();
        module.onResumeAfterSuper();
    }

    private void closeModule(CameraModule module) {
        module.onPauseBeforeSuper();
        module.onPauseAfterSuper();
        ((ViewGroup) mCameraModuleRootView).removeAllViews();
        ((ViewGroup) mCameraModuleRootView).clearDisappearingChildren();
    }

    public CameraUtil.CameraOpenErrorCallback getCameraOpenErrorCallback() {
        return mCameraOpenErrorCallback;
    }

    private CameraUtil.CameraOpenErrorCallback mCameraOpenErrorCallback =
            new CameraUtil.CameraOpenErrorCallback() {
                @Override
                public void onCameraDisabled(int cameraId) {
                    Toast.makeText(mContext, "can't open camera id: "+cameraId, Toast.LENGTH_SHORT).show();
                }};
}
