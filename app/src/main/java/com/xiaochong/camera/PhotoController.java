package com.xiaochong.camera;

import com.xiaochong.camera.ui.view.ShutterButton;

/**
 * Created by user on 6/8/16.
 */
public interface PhotoController extends ShutterButton.OnShutterButtonListener {

    public void onPreviewUIReady();

    public void onPreviewUIDestroyed();
}
