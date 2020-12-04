package com.zhy.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * @author:yhz
 * @time:2020/12/1
 * @email:309581534@qq.com
 * @describe:
 */
public abstract class BaseFloatingView extends LinearLayout implements View.OnClickListener {

    protected abstract int getLayoutId();

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;

    public BaseFloatingView(Context context) {
        super(context);
        init();
    }

    public BaseFloatingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    protected void init() {
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this);
        initWindowParams();
    }

    private void initWindowParams() {
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.flags = mWindowParams.flags
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowParams.gravity = Gravity.TOP | Gravity.START;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mWindowParams.x = 0;
        mWindowParams.y = 0;
        mWindowParams.format = PixelFormat.RGBA_8888;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    protected void setWindowTouch(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            //获取X坐标
            private int startX;
            //获取Y坐标
            private int startY;
            //初始化X的touch坐标
            private float startTouchX;
            //初始化Y的touch坐标
            private float startTouchY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = mWindowParams.x;
                        startY = mWindowParams.y;
                        startTouchX = event.getRawX();
                        startTouchY = event.getRawY();
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        mWindowParams.x = startX + (int) (event.getRawX() - startTouchX);
                        mWindowParams.y = startY + (int) (event.getRawY() - startTouchY);
                        //更新View的位置
                        mWindowManager.updateViewLayout(BaseFloatingView.this, mWindowParams);
                        return false;
                }
                return false;
            }
        });
    }

    public void addFloatView() {
        mWindowManager.addView(this, mWindowParams);
    }

    public void addFloatViewToCenter() {
        mWindowParams.gravity = Gravity.CENTER;
        mWindowManager.addView(this, mWindowParams);
    }

    public void addFloatViewToCenterTop() {
        mWindowParams.gravity = Gravity.CENTER | Gravity.TOP;
        mWindowManager.addView(this, mWindowParams);
    }

    public void removeFloatView() {
        mWindowManager.removeView(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeFloatView();
    }
}
