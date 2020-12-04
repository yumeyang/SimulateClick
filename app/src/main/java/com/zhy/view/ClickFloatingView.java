package com.zhy.view;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.zhy.view.androidsimulateclickdemon.R;

/**
 * @author:yhz
 * @time:2020/12/1
 * @email:309581534@qq.com
 * @describe:
 */
public class ClickFloatingView extends BaseFloatingView {

    private TextView tv_click;

    public ClickFloatingView(Context context) {
        super(context);
    }

    public ClickFloatingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        super.init();
        tv_click = findViewById(R.id.tv_click);
        tv_click.setOnClickListener(this);
        setWindowTouch(tv_click);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_click_floating;
    }

    @Override
    public void onClick(View v) {
        if (v == tv_click) {
            Toast.makeText(v.getContext(), "打开配置", Toast.LENGTH_SHORT).show();
        }
    }

    public Rect getViewRect() {
        int[] location = new int[2];
        getLocationOnScreen(location);
        Rect rect = new Rect();
        rect.left = location[0];
        rect.right = location[0] + getWidth();
        rect.top = location[1];
        rect.bottom = location[1] + getHeight();
        return rect;
    }
}
