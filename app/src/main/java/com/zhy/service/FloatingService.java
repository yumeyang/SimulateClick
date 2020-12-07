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
        getTimer().cancel();
        mService = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void dispatchGestureClick(int x, int y) {
        Path p = new Path();
        p.moveTo(x, y);
        p.lineTo(x, y);

        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(p, 0L, 100L));
        GestureDescription gesture = builder.build();
        dispatchGesture(gesture, new GestureResultCallback() {
            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
            }

            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
            }
        }, null);
    }

    private TimeFloatingView mFloatView;

    private TimeFloatingView getTimeFloatingView() {
        if (mFloatView == null) {
            mFloatView = new TimeFloatingView(this);
            mFloatView.setCallBack(new TimeFloatingView.CallBack() {
                @RequiresApi(api = Build.VERSION_CODES.N)
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
                public void timer(boolean open) {
                    if (open) {
                        getTimeFloatingView().addFloatViewToCenterTop();
                    } else {
                        getTimeFloatingView().removeFloatView();
                    }
                }

                @Override
                public void add() {
                    ClickFloatingView view = new ClickFloatingView(FloatingService.this);
                    view.addFloatViewToCenter();
                    mViews.add(view);
                }

                @Override
                public void del() {
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
                        }
                    });
                    dialog.addFloatViewToCenter();
                }


                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void start(boolean open) {
                    if (open) {
                        startTask();
                    } else {
                        stopTask();
                    }
                }

                @Override
                public void stop() {
                    FloatingService.this.stopSelf();
                }
            });
        }

        return toolView;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startTask() {
        clickStart();
        getTimer().start();
    }

    public void stopTask() {
        getTimer().cancel();
        clickStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void clickStart() {
        for (ClickFloatingView view : mViews) {
            view.setVisibility(View.GONE);
            dispatchGestureClick(view.getCenterX(), view.getCenterY());
        }
    }

    private void clickStop() {
        for (ClickFloatingView view : mViews) {
            view.setVisibility(View.VISIBLE);
        }
    }


    private CountDownTimer mTimer;

    private CountDownTimer getTimer() {
        if (mTimer == null) {
            mTimer = new CountDownTimer(10000, 100) {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onTick(long millisUntilFinished) {
                    clickStart();
                }

                @Override
                public void onFinish() {
                    getToolView().start(false);
                    clickStop();
                }
            };
        }
        return mTimer;
    }
}