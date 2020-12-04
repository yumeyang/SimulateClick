package com.zhy.view;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.zhy.view.androidsimulateclickdemon.R;

/**
 * @author:yhz
 * @time:2020/12/2
 * @email:309581534@qq.com
 * @describe:
 */
public class TimeFloatingView extends BaseFloatingView {

    private TextView tv_time;
    private CountDownTimer mTimer;

    private String mTimeFrame;

    public TimeFloatingView(Context context) {
        super(context);
    }

    public TimeFloatingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_time_float;
    }

    @Override
    protected void init() {
        super.init();
        tv_time = findViewById(R.id.tv_time);
        setWindowTouch(tv_time);

        mTimer = new CountDownTimer(Integer.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFinish() {

            }
        };
        mTimer.start();
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                long sys_time = System.currentTimeMillis();
                CharSequence sys_time_str = DateFormat.format("HH:mm:ss", sys_time);
                if (mCallBack != null && sys_time_str.equals(mTimeFrame)) {
                    mCallBack.getToTime();
                }

                tv_time.setText(sys_time_str);
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTimer.cancel();
    }

    private CallBack mCallBack;

    public void setCallBack(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public interface CallBack {
        void getToTime();
    }
}
