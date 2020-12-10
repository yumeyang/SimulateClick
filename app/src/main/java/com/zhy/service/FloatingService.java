package com.zhy.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.CountDownTimer;
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
        builder.addStroke(new GestureDescription.StrokeDescription(p, 0L, 1L));
        GestureDescription gesture = builder.build();
        sleep(1);
        dispatchGesture(gesture, new GestureResultCallback() {
        }, null);
    }

    private TimeFloatingView mFloatView;

    private TimeFloatingView getTimeFloatingView() {
        if (mFloatView == null) {
            mFloatView = new TimeFloatingView(this);
            mFloatView.setCallBack(new TimeFloatingView.CallBack() {

                @Override
                public void getToTime() {
                    getTimeFloatingView().stop();
                    getTimeFloatingView().setVisibility(View.GONE);
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
                            for (ClickFloatingView view : mViews) {
                                view.setAutoTime(time);
                            }
                            getTimeFloatingView().setAutoTime(time);
                            showClickFloatView(false);
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
        toClickFloatView();
        getToolView().setTvStop(1);
        getTimer().start();
    }

    public void stopTask() {
        getTimeFloatingView().start();
        getTimeFloatingView().setVisibility(View.VISIBLE);
        getToolView().setTvStop(0);
        getTimer().cancel();
        showClickFloatView(true);
    }

    private void toClickFloatView() {
        for (ClickFloatingView view : mViews) {
            dispatchGestureClick(view.getCenterX(), view.getCenterY());
        }
    }

    private void showClickFloatView(boolean show) {
        for (ClickFloatingView view : mViews) {
            if (show) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.hideAndSaveXY();
            }
        }
    }

    private CountDownTimer mTimer;

    private CountDownTimer getTimer() {
        if (mTimer == null) {
            mTimer = new CountDownTimer(10000, 50) {

                @Override
                public void onTick(long millisUntilFinished) {
                    toClickFloatView();
                }

                @Override
                public void onFinish() {
                    getTimeFloatingView().start();
                    getTimeFloatingView().setVisibility(View.VISIBLE);
                    getToolView().setTvStop(0);
                    showClickFloatView(true);
                }
            };
        }
        return mTimer;
    }

    private void destroy() {
        for (ClickFloatingView view : mViews) {
            view.removeFloatView();
            mViews.remove(view);
        }

        getToolView().removeFloatView();
        getTimeFloatingView().removeFloatView();
        getTimer().cancel();
        mService = null;
    }
}