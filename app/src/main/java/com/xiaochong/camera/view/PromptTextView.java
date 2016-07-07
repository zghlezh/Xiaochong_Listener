package com.xiaochong.camera.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by user on 7/4/16.
 */
public class PromptTextView extends TextView {
    private static final int TEXT_CHANGE_INTERVAL = 500;
    private String mComma = ".";
    private String mOldText;
    private String mText;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!TextUtils.isEmpty(getText())) {
                if (mComma.length() <= 2) {
                    mComma += ".";
                } else {
                    mComma = ".";
                }
                mText = mOldText + mComma;
                Log.i("PromptTextView", "the mtext is "+mText);
                setText(mText);
                invalidate();
            }
            if (handler != null) {
                handler.sendMessageDelayed(handler.obtainMessage(), TEXT_CHANGE_INTERVAL);
            }
        }
    };

    public void startToAnimate() {
        mOldText = getText().toString();
        handler.sendMessageDelayed(handler.obtainMessage(), TEXT_CHANGE_INTERVAL);
    }

    public void relase() {
        handler = null;
    }

    public PromptTextView(Context context) {
        super(context);
    }

    public PromptTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
