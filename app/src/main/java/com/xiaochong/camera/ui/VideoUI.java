package com.xiaochong.camera.ui;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaochong.camera.CameraActivity;
import com.xiaochong.camera.R;
import com.xiaochong.camera.VideoController;
import com.xiaochong.camera.ui.view.PromptTextView;
import com.xiaochong.camera.ui.view.ShutterButton;

/**
 * Created by user on 6/7/16.
 */
public class VideoUI implements
        SurfaceHolder.Callback, View.OnClickListener {

    private static final String TAG = "CAM_VideoUI";
    private CameraActivity mActivity;
    private View mRootView;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;

    private VideoController mController;
    private View mPreviewCover;
    private TextView mRecordTime;
    private PromptTextView mProptText;
    private ImageView mPreviewThumb;
    private ShutterButton mShutterButton;


    public VideoUI(CameraActivity activity, VideoController controller, View parent) {
        initView(activity, controller, parent);
    }

    private void initView(CameraActivity activity, VideoController controller, View parent) {
        mActivity = activity;
        mController = controller;
        mRootView = parent;
        mActivity.getLayoutInflater().inflate(R.layout.video_module, (ViewGroup) mRootView, true);
        mPreviewCover = mRootView.findViewById(R.id.preview_cover);
        mSurfaceView = (SurfaceView) mRootView.findViewById(R.id.preview_content);
        mProptText = (PromptTextView) mRootView.findViewById(R.id.text_prompt);
        mProptText.setText(R.string.video_auto_take);
        mProptText.startToAnimate();
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mRecordTime = (TextView) mRootView.findViewById(R.id.recording_time);
        mPreviewThumb = (ImageView) mRootView.findViewById(R.id.preview_thumb);
        mShutterButton = (ShutterButton) mRootView.findViewById(R.id.shutter_button);
        mShutterButton.setMode(ShutterButton.MODE_RECORD);
        mShutterButton.updateImageResource(false);
        mShutterButton.setOnShutterButtonListener(mController);
        mShutterButton.setVisibility(View.VISIBLE);
        mShutterButton.requestFocus();
        mShutterButton.enableTouch(true);
        mShutterButton.setOnClickListener(this);
        mPreviewThumb.setOnClickListener(this);
    }

    public void relaseText() {
        mProptText.relase();
    }

    public void showTimeLapseUI(boolean enable) {
        if (mRecordTime != null) {
            mRecordTime.setVisibility(enable ? View.VISIBLE : View.GONE);
        }
    }

    public SurfaceHolder getSurfaceHolder() {
        return mHolder;
    }

    public void hideSurfaceView() {
        mSurfaceView.setVisibility(View.GONE);
        //mTextureView.setVisibility(View.VISIBLE);
    }

    public void showSurfaceView() {
        mSurfaceView.setVisibility(View.VISIBLE);
       // mTextureView.setVisibility(View.GONE);
    }


    public void enableShutter(boolean enable) {
        if (mShutterButton != null) {
            mShutterButton.setEnabled(enable);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        mController.onPreviewUIReady();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mPreviewCover.getVisibility() != View.GONE) {
            mPreviewCover.setVisibility(View.GONE);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder = null;
        mController.onPreviewUIDestroyed();
        Log.i(TAG, "SurfaceTexture destroyed");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shutter_button:
                mShutterButton.click();
                break;
            case R.id.camera_switcher:

                break;
            case R.id.preview_thumb:
                //Todo enter Gallery to view

                break;
        }
    }

    public void updateShutterButton(boolean isRecording) {
        mShutterButton.updateImageResource(isRecording);
    }
}
