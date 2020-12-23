package com.zhy.view;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.zhy.simulate.click.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author:yhz
 * @time:2020/12/2
 * @email:309581534@qq.com
 * @describe:
 */
public class TimeFloatingView extends BaseFloatingView {

    private TextView tv_time;
    private CountDownTimer mTimer;

    private long mAutoTime;

    public void setAutoTime(long time) {
        mAutoTime = time;
    }

    public TimeFloatingView(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_float_time;
    }

    @Override
    protected void init() {
        super.init();
        tv_time = findViewById(R.id.tv_time);
        setWindowTouch(tv_time);

        mTimer = new CountDownTimer(Integer.MAX_VALUE, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss S");
                CharSequence sys_time_str = sdf.format(new Date());
                tv_time.setText(sys_time_str);

                if (mCallBack != null && mAutoTime > 0) {
                    long sys_time = System.currentTimeMillis();
                    if (sys_time >= mAutoTime) {
                        mAutoTime = 0;
                        mCallBack.getToTime();
                    }
                }
            }

            @Override
            public void onFinish() {

            }
        };
    }

    @Override
    public void addFloatViewToCenterTop() {
        super.addFloatViewToCenterTop();
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    public void stop() {
        mTimer.cancel();
    }

    public void start() {
        mTimer.start();
    }

    private CallBack mCallBack;

    public void setCallBack(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public interface CallBack {
        void getToTime();
    }
}
