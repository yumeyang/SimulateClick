package com.zhy.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
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


    private static final long CLICK_DURATION = 10000;
    private static final long CLICK_SLEEP_TIME = 50;

    private boolean mEndClickTask;

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
        builder.addStroke(new GestureDescription.StrokeDescription(p, 0L, 50L));
        GestureDescription gesture = builder.build();
        sleep(50);
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
                    startClickTask();
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
                        }
                    });
                    dialog.addFloatViewToCenter();
                }

                @Override
                public void stop(int status) {
                    if (status == 1) {
                        stopClickTask();
                    } else {
                        disableSelf();
                    }
                }
            });
        }

        return toolView;
    }

    private void startClickTask() {
        int size = mViews.size();
        if (size == 0) {
            return;
        }

        hideNormalView();

        int click_num = (int) (CLICK_DURATION / CLICK_SLEEP_TIME) * size;

        for (int i = 0; i < click_num || mEndClickTask; i++) {
            for (ClickFloatingView view : mViews) {
                dispatchGestureClick(view.getCenterX(), view.getCenterY());
            }
        }

        showNormalView();
    }

    private void stopClickTask() {
        mEndClickTask = true;
        showNormalView();
    }

    private void showNormalView() {
        getTimeFloatingView().start();
        getTimeFloatingView().setVisibility(View.VISIBLE);
        getToolView().setTvStop(0);
        showClickFloat();
    }

    private void hideNormalView() {
        hideAndSaveXY();
        getTimeFloatingView().stop();
        getTimeFloatingView().setVisibility(View.GONE);
        getToolView().setTvStop(1);
    }

    private void showClickFloat() {
        for (ClickFloatingView view : mViews) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void hideAndSaveXY() {
        for (ClickFloatingView view : mViews) {
            view.hideAndSaveXY();
        }
    }

    private void destroy() {
        mEndClickTask = true;

        getToolView().removeFloatView();
        getTimeFloatingView().removeFloatView();

        for (ClickFloatingView view : mViews) {
            view.removeFloatView();
            mViews.remove(view);
        }

        mService = null;
    }
}