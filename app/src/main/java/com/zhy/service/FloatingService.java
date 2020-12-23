package com.zhy.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;

import com.zhy.view.SettingFloatingView;
import com.zhy.view.ClickFloatingView;
import com.zhy.view.TimeFloatingView;
import com.zhy.view.ToolFloatingView;

import java.util.ArrayList;

import static android.os.SystemClock.sleep;

@RequiresApi(api = Build.VERSION_CODES.N)
public class FloatingService extends AccessibilityService {

    public static FloatingService mService;

    public static boolean isStart() {
        return mService != null;
    }

    public static void addToolFloating(Activity activity) {
        if (!isStart()) {
            Intent intent = new Intent(activity, FloatingService.class);
            activity.startService(intent);
        }
        mService.getToolView().addFloatView();
        mService.getTimeFloatingView().addFloatViewToCenterTop();
    }

    private final ArrayList<ClickFloatingView> mViews = new ArrayList<>();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mService = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroy();
    }

    public void dispatchGestureClick(int x, int y) {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(x, y);
        p.lineTo(x, y);
        builder.addStroke(new GestureDescription.StrokeDescription(p, 0L, SLEEP_TIME));
        GestureDescription gesture = builder.build();
        sleep(SLEEP_TIME);
        dispatchGesture(gesture, new GestureResultCallback() {
        }, null);
        Log.e("FloatingService", "FloatingService SLEEP_TIME = " + SLEEP_TIME + " mClickedNum = " + mClickedNum);
    }

    private TimeFloatingView mFloatView;

    private TimeFloatingView getTimeFloatingView() {
        if (mFloatView == null) {
            mFloatView = new TimeFloatingView(this);
            mFloatView.setCallBack(new TimeFloatingView.CallBack() {

                @Override
                public void getToTime() {
                    startTask();
                }
            });
        }
        return mFloatView;
    }

    private ToolFloatingView toolView;

    private ToolFloatingView getToolView() {
        if (toolView == null) {
            toolView = new ToolFloatingView(this);
            toolView.setCallBack(new ToolFloatingView.CallBack() {
                @Override
                public void add() {
                    ClickFloatingView view = new ClickFloatingView(FloatingService.this);
                    view.addFloatViewToCenter();
                    mViews.add(view);
                }

                @Override
                public void del() {
                    if (mViews.size() == 0) {
                        return;
                    }
                    ClickFloatingView view = mViews.get(mViews.size() - 1);
                    view.removeFloatView();
                    mViews.remove(view);
                }

                @Override
                public void config() {
                    SettingFloatingView dialog = new SettingFloatingView(FloatingService.this);
                    dialog.setCallBack(new SettingFloatingView.CallBack() {
                        @Override
                        public void save(long time) {
                            getTimeFloatingView().setAutoTime(time);
                            getToolView().setTvStop(1);
                        }
                    });
                    dialog.addFloatViewToCenter();
                }

                @Override
                public void stop(int status) {
                    if (status == 1) {
                        stopTask();
                    } else {
                        disableSelf();
                    }
                }
            });
        }

        return toolView;
    }

    public void startTask() {
        hideClickFloatView();
        getToolView().setTvStop(1);

        mLooper.sendEmptyMessage(MSG_TYPE_0);
    }

    public void stopTask() {
        mLooper.removeMessages(MSG_TYPE_0);
        mLooper.removeMessages(MSG_TYPE_1);

        mClickedNum = 0;
        setRelatedView();
    }

    private void toClickFloatView() {
        for (ClickFloatingView view : mViews) {
            dispatchGestureClick(view.getCenterX(), view.getCenterY());
        }
    }

    private void showClickFloatView() {
        for (ClickFloatingView view : mViews) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void hideClickFloatView() {
        for (ClickFloatingView view : mViews) {
            view.saveXY();
            view.setVisibility(View.GONE);
        }
    }

    private void setRelatedView() {
        getToolView().setTvStop(0);
        showClickFloatView();
    }

    private final Handler mLooper = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_TYPE_0) {
                if (mClickedNum > LIMIT_CLICK_NUM) {
                    mLooper.sendEmptyMessage(MSG_TYPE_1);
                } else {
                    toClickFloatView();
                    mLooper.sendEmptyMessage(MSG_TYPE_0);
                }
                mClickedNum++;
            } else {
                mClickedNum = 0;
                setRelatedView();
            }
        }
    };

    private void destroy() {
        for (ClickFloatingView view : mViews) {
            view.removeFloatView();
        }
        mViews.clear();

        getToolView().removeFloatView();
        getTimeFloatingView().removeFloatView();

        mLooper.removeMessages(MSG_TYPE_0);
        mLooper.removeMessages(MSG_TYPE_1);

        mService = null;
    }

    private int mClickedNum;//已经点击的次数

    private static final int LIMIT_CLICK_NUM = 100;//循环执行点击100次
    private static final long SLEEP_TIME = 100;//点击执行时间（系统睡眠时间）,单位毫秒
    private static final int MSG_TYPE_0 = 0;//循环执行
    private static final int MSG_TYPE_1 = 1;//循环停止
}