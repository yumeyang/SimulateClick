package com.zhy.view;

import android.content.Context;
import android.widget.TextView;

import com.zhy.simulate.click.R;


/**
 * @author:yhz
 * @time:2020/12/1
 * @email:309581534@qq.com
 * @describe:
 */
public class ClickFloatingView extends BaseFloatingView {

    private long mAutoTime;

    public void setAutoTime(long time) {
        mAutoTime = time;
    }

    private TextView tv_click;

    private int mX;
    private int mY;

    public ClickFloatingView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        super.init();
        tv_click = findViewById(R.id.tv_click);
        setWindowTouch(tv_click);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_float_click;
    }

    public int getCenterX() {
        return mX;
    }

    public int getCenterY() {
        return mY;
    }

    public void hideAndSaveXY() {
        int[] location = new int[2];
        getLocationOnScreen(location);
        mX = location[0] + getWidth() / 2;
        mY = location[1] + getHeight() / 2;
    }
}
