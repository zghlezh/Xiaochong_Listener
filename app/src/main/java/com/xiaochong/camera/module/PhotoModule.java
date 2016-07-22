package com.xiaochong.camera.module;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;

import com.xiaochong.camera.CameraActivity;
import com.xiaochong.camera.PhotoController;
import com.xiaochong.camera.ui.PhotoUI;
import com.xiaochong.camera.util.local.CameraUtil;
import com.xiaochong.camera.util.local.GetMediaImpl;
import com.xiaochong.camera.ui.view.ShutterButton;

import java.io.IOException;
import java.util.List;

/**
 * Created by user on 6/7/16.
 */
public class PhotoModule
        implements CameraModule,
        PhotoController,
        ShutterButton.OnShutterButtonListener {
    public static final String TAG = "PhotoModule";
    private boolean mSafeToTakePicture = false;
    private static final int SCREEN_DELAY = 2 * 60 * 1000;
    private static final int START_PREVIEW = 1;
    private static final int CLEAR_SCREEN_DELAY = 3;
    private static final int SWITCH_CAMERA = 6;
    private static final int SWITCH_CAMERA_START_ANIMATION = 7;
    private static final int CAMERA_OPEN_DONE = 8;
    private static final int OPEN_CAMERA_FAIL = 9;
    private static final int CAMERA_DISABLED = 10;
    private static final int ON_PREVIEW_STARTED = 15;
    private static final int TAKE_PHOTO_ONCE = 16;
    private static final int TAKE_PHOTO_DELAY_TIME = 3000;

    private CameraActivity mActivity;
    private Camera mCamera;
    private boolean mPaused;
    private int mCameraId;
    private View mRootView;

    private PhotoUI mUI;
    private OpenCameraThread mOpenCameraThread = null;
    private Handler mHandler = new MainHandler();

    public String mFilePath;

    private class OpenCameraThread extends Thread {
        @Override
        public void run() {
            openCamera();
            startPreview();
        }
    }

    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (camera == null) {
                return;
            }
            try {
                mFilePath = CameraUtil.generatePath(CameraUtil.PATH_TYPE_PHOTO);
                ImageSaveTask imageSaveTask =
                        new ImageSaveTask(data, mFilePath, mOnMediaSavedListener);
                imageSaveTask.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private CameraUtil.OnMediaSavedListener mOnMediaSavedListener =
            new CameraUtil.OnMediaSavedListener() {
                @Override
                public void onMediaSaved(Uri uri) {
                    //notify the media is saved .
                    if (mCamera != null) {
                        mCamera.startPreview();
                        mSafeToTakePicture = true;
                    }
                    //Toast.makeText(mActivity, mActivity.getText(R.string.save_pic) + mFilePath,
                    //        Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra(GetMediaImpl.GET_PHOTO_PATH, mFilePath);
                    mActivity.setResult(GetMediaImpl.KEY_GET_PHOTO, intent);
                    mActivity.finish();
                }
            };

    private class MainHandler extends Handler {
        public MainHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_PREVIEW: {
                    startPreview();
                    break;
                }

                case CLEAR_SCREEN_DELAY: {
                    mActivity.getWindow().clearFlags(
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    break;
                }

                case SWITCH_CAMERA: {
                    switchCamera();
                    break;
                }

                case SWITCH_CAMERA_START_ANIMATION: {
                    // TODO: Need to revisit
                    break;
                }

                case CAMERA_OPEN_DONE: {
                    onCameraOpened();
                    break;
                }

                case OPEN_CAMERA_FAIL: {
                    //Todo fail situation
                    break;
                }

                case CAMERA_DISABLED: {
                    //Todo
                    break;
                }

                case ON_PREVIEW_STARTED: {
                    onPreviewStarted();
                    break;
                }

                case TAKE_PHOTO_ONCE: {
                    //Toast.makeText(mActivity, "camera opened ~~~~~~", Toast.LENGTH_SHORT).show();
                    mCamera.autoFocus(focusCallback);
                    break;
                }
            }
        }
    }

    private void startPreview() {
        if (mPaused || mCamera == null) {
            return;
        }

        Log.d(TAG, "startPreview");

        SurfaceHolder sh = null;
        if (mUI != null) {
            sh = mUI.getSurfaceHolder();
        }
        try {
            mCamera.setPreviewDisplay(sh);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.startPreview();
        mSafeToTakePicture = true;
    }

    private void switchCamera() {
        //Todo siwtch to forground camera
    }


    private void openCamera() {
        // We need to check whether the activity is paused before long
        // operations to ensure that onPause() can be done ASAP.
        if (mPaused) {
            return;
        }
        Log.i(TAG, "Open camera device.");
        mCamera = CameraUtil.openCamera(
                mActivity, mCameraId, mHandler,
                mActivity.getCameraOpenErrorCallback());
        if (mCamera == null) {
            Log.e(TAG, "Failed to open camera:" + mCameraId);
            mHandler.sendEmptyMessage(OPEN_CAMERA_FAIL);
            return;
        }
        mHandler.sendEmptyMessageDelayed(CAMERA_OPEN_DONE, 100);
        return;
    }

    private void onCameraOpened() {
        int rotation = CameraUtil.getDisplayOrientation(mActivity, mCameraId);
        mCamera.setDisplayOrientation(rotation);
    }

    private void onPreviewStarted() {
        //Todo
    }

    @Override
    public void init(CameraActivity activity, View parent) {
        mActivity = activity;
        mRootView = parent;
        mCameraId = CameraUtil.getCameraId();
//        mCameraId = CameraUtil.getCIDFromManager(mActivity);
        mUI = new PhotoUI(mActivity, this, parent);
        if (mOpenCameraThread == null && !mActivity.mIsModuleSwitchInProgress) {
            mOpenCameraThread = new OpenCameraThread();
            mOpenCameraThread.start();
        }
        keepScreenOnAwhile();
        Message msg = mHandler.obtainMessage();
        msg.what = TAKE_PHOTO_ONCE;
        mHandler.sendMessageDelayed(msg, TAKE_PHOTO_DELAY_TIME);
    }



    @Override
    public void onPauseAfterSuper() {
        try {
            if (mOpenCameraThread != null) {
                mOpenCameraThread.join();
            }
        } catch (InterruptedException ex) {
            // ignore
        }
        mOpenCameraThread = null;
        // Remove the messages and runnables in the queue.
        mHandler.removeCallbacksAndMessages(null);
        closeCamera();
        mUI.relaseText();
    }

    private void closeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        resetScreenOn();
    }

    //need set mPause flag to consider the situation of return to launcher.
    @Override
    public void onPauseBeforeSuper() {
        mPaused = true;
    }

    @Override
    public void onResumeBeforeSuper() {
        mPaused = false;
    }

    @Override
    public void onResumeAfterSuper() {
        if (mOpenCameraThread == null) {
            mOpenCameraThread = new OpenCameraThread();
            mOpenCameraThread.start();
        }
    }

    @Override
    public void onPreviewUIReady() {
        //Todo
    }

    @Override
    public void onPreviewUIDestroyed() {
        //Todo
    }


    Camera.AutoFocusCallback focusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                Camera.Parameters params = mCamera.getParameters();
                params.setPictureFormat(PixelFormat.JPEG);
                params.setJpegQuality(100);

                int size[] = getPictureSize(params);
                params.setPictureSize(size[0], size[1]);
                mCamera.setParameters(params);
                takePhoto();
            }
        }
    };

    private int[] getPictureSize(Camera.Parameters params) {
        List<Camera.Size> picSize = params.getSupportedPictureSizes();
        int flag = (picSize.size() / 2) + 1;
        Log.i("qinghao.wu ==", "the get pic size is "+picSize.get(flag).width+" and height is "+picSize.get(flag).height);
        return new int[]{picSize.get(flag).width, picSize.get(flag).height};
    }

    private void resetScreenOn() {
        mHandler.removeMessages(CLEAR_SCREEN_DELAY);
        mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void keepScreenOnAwhile() {
        mHandler.removeMessages(CLEAR_SCREEN_DELAY);
        mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mHandler.sendEmptyMessageDelayed(CLEAR_SCREEN_DELAY, SCREEN_DELAY);
    }

    @Override
    public void onShutterButtonFocus(boolean pressed) {
        //Todo
    }

    @Override
    public void onShutterButtonClick() {
        if (mPaused) {
            return;
        }
        //play sound and storage
        mCamera.autoFocus(focusCallback);
    }

    @Override
    public void onShutterButtonLongClick() {
        //Todo focus
    }

    public void takePhoto() {
        if (mCamera == null) {
            return;
        }
        Log.i(TAG, "take photo started");
        if (mSafeToTakePicture) {
            mCamera.takePicture(null, null, pictureCallback);
            mSafeToTakePicture = false;
        }
    }

    private class ImageSaveTask extends AsyncTask<Void, Void, Uri> {
        private byte[] data;
        private String path;
        private CameraUtil.OnMediaSavedListener listener;

        public ImageSaveTask(byte[] data, String path,
                             CameraUtil.OnMediaSavedListener listener) {
            this.data = data;
            this.path = path;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            // do nothing.
        }

        @Override
        protected Uri doInBackground(Void... v) {
                // Decode bounds
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
            CameraUtil.writeFile(path, data, bm);
            return null;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            if (listener != null) listener.onMediaSaved(uri);
        }
    }
}
