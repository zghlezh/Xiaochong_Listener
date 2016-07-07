package com.xiaochong.camera.ui;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaochong.camera.CameraActivity;
import com.xiaochong.camera.PhotoController;
import com.xiaochong.camera.R;
import com.xiaochong.camera.view.PromptTextView;
import com.xiaochong.camera.view.ShutterButton;

/**
 * Created by user on 6/7/16.
 */
public class PhotoUI
        implements SurfaceHolder.Callback, View.OnClickListener {

    private static final String TAG = "CAM_UI";
    private CameraActivity mActivity;
    private View mRootView;
    private ShutterButton mShutterButton;
    private FrameLayout mCameraControls;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private PromptTextView mProptText;
    private View mPreviewCover;
    private final Object mSurfaceTextureLock = new Object();
    private PhotoController mController;
    //for thumb show
    private ImageView mPreviewThumb;


    public PhotoUI(CameraActivity activity, PhotoController controller, View parent) {
        Log.i(TAG, "PhotoUI create");
        mActivity = activity;
        mController = controller;
        mRootView = parent;
        mActivity.getLayoutInflater().inflate(R.layout.photo_module,
                (ViewGroup) mRootView, true);
        mPreviewCover = mRootView.findViewById(R.id.preview_cover);
        mProptText = (PromptTextView) mRootView.findViewById(R.id.text_prompt);
        mProptText.setText(R.string.photo_auto_take);
        mProptText.startToAnimate();
        // display the view
        mSurfaceView = (SurfaceView) mRootView.findViewById(R.id.preview_content);
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mShutterButton = (ShutterButton) mRootView.findViewById(R.id.shutter_button);
        mShutterButton.setMode(ShutterButton.MODE_TAKE_PHOTO);
        mShutterButton.updateImageResource(false);
        mShutterButton.setOnClickListener(this);
        mShutterButton.setOnShutterButtonListener(mController);
        mCameraControls = (FrameLayout) mRootView.findViewById(R.id.camera_controls);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shutter_button:
                Log.i(TAG, "shutter_button click");
                mShutterButton.click();
            break;
        }
    }

    public void relaseText() {
        mProptText.relase();
    }

    public SurfaceHolder getSurfaceHolder() {
        return mHolder;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        synchronized (mSurfaceTextureLock) {
            Log.i(TAG, "mHolder ready.");
            mHolder = holder;
            mController.onPreviewUIReady();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Make sure preview cover is hidden if preview data is available.
        if (mPreviewCover.getVisibility() != View.GONE) {
            mPreviewCover.setVisibility(View.GONE);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (mSurfaceTextureLock) {
            mHolder = null;
            mController.onPreviewUIDestroyed();
            Log.i(TAG, "SurfaceTexture destroyed");
        }
    }
}
