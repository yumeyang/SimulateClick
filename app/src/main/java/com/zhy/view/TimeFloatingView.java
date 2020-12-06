package com.zhy.view;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.zhy.view.androidsimulateclickdemon.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author:yhz
 * @time:2020/12/2
 * @email:309581534@qq.com
 * @describe:
 */
public class TimeFloatingView extends BaseFloatingView
{

    private TextView tv_time;
    private CountDownTimer mTimer;

    private long mAutoTime;

    public void setAutoTime(long time)
    {
        mAutoTime = time;
    }

    public TimeFloatingView(Context context)
    {
        super(context);
    }

    public TimeFloatingView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected int getLayoutId()
    {
        return R.layout.view_float_time;
    }

    @Override
    protected void init()
    {
        super.init();
        tv_time = findViewById(R.id.tv_time);
        setWindowTouch(tv_time);

        mTimer = new CountDownTimer(Integer.MAX_VALUE, 100)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFinish()
            {

            }
        };
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (msg.what == 1)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss SS");
                CharSequence sys_time_str = sdf.format(new Date());
                if (mCallBack != null && mAutoTime > 0)
                {
                    long sys_time = System.currentTimeMillis();
                    if (mAutoTime >= sys_time)
                    {
                        mCallBack.getToTime();
                    }
                }

                tv_time.setText(sys_time_str);
            }
        }
    };

    @Override
    public void addFloatViewToCenterTop()
    {
        super.addFloatViewToCenterTop();
        mTimer.start();
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        mTimer.cancel();
    }

    private CallBack mCallBack;

    public void setCallBack(CallBack mCallBack)
    {
        this.mCallBack = mCallBack;
    }

    public interface CallBack
    {
        void getToTime();
    }
}
