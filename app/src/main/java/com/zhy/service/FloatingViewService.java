package com.zhy.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.zhy.dialog.SettingDialog;
import com.zhy.view.AbstractTF;
import com.zhy.view.ClickFloatingView;
import com.zhy.view.TimeFloatingView;
import com.zhy.view.ToolFloatingView;
import com.zhy.view.Utils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static android.os.SystemClock.sleep;

public class FloatingViewService extends AccessibilityService {

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, FloatingViewService.class);
        activity.startService(intent);
    }

    public static FloatingViewService mService;


    private final ArrayList<ClickFloatingView> mViews = new ArrayList<>();

    public FloatingViewService() {
    }

    public static boolean isStart() {
        return mService != null;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {
        mService = null;
    }

    /**
     * 点击指定位置
     * 注意7.0以上的手机才有此方法，请确保运行在7.0手机上
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean dispatchGestureClick(int x, int y) {
        boolean res = false;
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(x, y);
        p.lineTo(x, y);
        builder.addStroke(new GestureDescription.StrokeDescription(p, 0L, 100L));
        GestureDescription gesture = builder.build();
        Log.d("", "点击了位置" + "(" + x + "," + y + ")");
        sleep(200);
        res = dispatchGesture(gesture, new GestureResultCallback() {
        }, null);
        return res;
    }

    /**
     * 查找全部匹配的控件
     *
     * @param tfs 匹配条件，多个AbstractTF是&&的关系，如：
     *            AbstractTF.newContentDescription("表情", true),AbstractTF.newClassName(AbstractTF.ST_IMAGEVIEW)
     *            表示描述内容是'表情'并且是imageview的控件
     */
    @NonNull
    public List<AccessibilityNodeInfo> findAll(@NonNull AbstractTF... tfs) {
        if (tfs.length == 0) throw new InvalidParameterException("AbstractTF不允许传空");

        ArrayList<AccessibilityNodeInfo> list = new ArrayList<>();
        AccessibilityNodeInfo rootInfo = getRootInActiveWindow();
        if (rootInfo == null) return list;

        int idTextTFCount = 0, idTextIndex = 0;
        for (int i = 0; i < tfs.length; i++) {
            if (tfs[i] instanceof AbstractTF.IdTextTF) {
                idTextTFCount++;
                idTextIndex = i;
            }
        }
        switch (idTextTFCount) {
            case 0://id或text数量为0，直接循环查找
                findAllRecursive(list, rootInfo, tfs);
                break;
            case 1://id或text数量为1，先查出对应的id或text，然后再循环
                List<AccessibilityNodeInfo> listIdText = ((AbstractTF.IdTextTF) tfs[idTextIndex]).findAll(rootInfo);
                if (Utils.isEmptyArray(listIdText)) {
                    break;
                }
                if (tfs.length == 1) {
                    list.addAll(listIdText);
                } else {
                    for (AccessibilityNodeInfo info : listIdText) {
                        boolean isOk = true;
                        for (AbstractTF tf : tfs) {
                            if (!tf.checkOk(info)) {
                                isOk = false;
                                break;
                            }
                        }
                        if (isOk) {
                            list.add(info);
                        } else {
                            info.recycle();
                        }
                    }
                }
                break;
            default:
                throw new RuntimeException("由于时间有限，并且多了也没什么用，所以IdTF和TextTF只能有一个");
        }
        rootInfo.recycle();
        return list;
    }

    /**
     * @param tfs 由于是递归循环，会忽略IdTF和TextTF
     */
    public static void findAllRecursive(List<AccessibilityNodeInfo> list, AccessibilityNodeInfo parent, @NonNull AbstractTF... tfs) {
        if (parent == null || list == null) return;
        if (tfs.length == 0) throw new InvalidParameterException("AbstractTF不允许传空");

        for (int i = 0; i < parent.getChildCount(); i++) {
            AccessibilityNodeInfo child = parent.getChild(i);
            if (child == null) continue;
            boolean isOk = true;
            for (AbstractTF tf : tfs) {
                if (!tf.checkOk(child)) {
                    isOk = false;
                    break;
                }
            }
            if (isOk) {
                list.add(child);
            } else {
                findAllRecursive(list, child, tfs);
                child.recycle();
            }
        }
    }

    private TimeFloatingView mFloatView;

    private TimeFloatingView showTimeFloatingView() {
        if (mFloatView == null) {
            mFloatView = new TimeFloatingView(this);
            mFloatView.addFloatViewToCenterTop();
        }
        return mFloatView;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mService = this;
        ToolFloatingView toolView = new ToolFloatingView(this);
        toolView.setCallBack(new ToolFloatingView.CallBack() {
            @Override
            public void addTimer() {
                showTimeFloatingView();
            }

            @Override
            public void add() {
                ClickFloatingView view = new ClickFloatingView(FloatingViewService.this);
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
                SettingDialog dialog = new SettingDialog(FloatingViewService.this);
                dialog.show();
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void start() {
                for (ClickFloatingView view : mViews) {
                    view.setVisibility(View.GONE);
                    boolean isSuccess = dispatchGestureClick(view.getViewRect().left + 20, view.getViewRect().top + 20);
//                    AbstractTF tf = AbstractTF.newRect(view.getViewRect());
//                    List<AccessibilityNodeInfo> list = findAll(tf);
//                    for (AccessibilityNodeInfo info : list) {
//                        clickView(info);
//                    }
                    if (isSuccess) {
                        //view.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        toolView.addFloatView();
    }

    /**
     * 点击该控件
     *
     * @return true表示点击成功
     */
    public static boolean clickView(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null) {
            if (nodeInfo.isClickable()) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return true;
            } else {
                AccessibilityNodeInfo parent = nodeInfo.getParent();
                if (parent != null) {
                    boolean b = clickView(parent);
                    parent.recycle();
                    if (b) return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mService = null;
    }

    //
    //                以下代码模拟点击文本编辑框
    //                et.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, et.getLeft()+5, et.getTop()+5, 0));
    //                et.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, et.getLeft()+5, et.getTop()+5, 0));

    //  et.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, et.getLeft()+5, et.getTop()+5, 0));
    //                et.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, et.getLeft()+5, et.getTop()+5, 0));
}