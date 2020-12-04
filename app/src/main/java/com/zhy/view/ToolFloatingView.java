package com.zhy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.zhy.view.androidsimulateclickdemon.R;

/**
 * @author:yhz
 * @time:2020/12/1
 * @email:309581534@qq.com
 * @describe:
 */
public class ToolFloatingView extends BaseFloatingView {

    private TextView tv_timer;
    private TextView tv_add;
    private TextView tv_del;
    private TextView tv_config;
    private TextView tv_start;

    public ToolFloatingView(Context context) {
        super(context);
    }

    public ToolFloatingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    protected void init() {
        super.init();
        View ll_parent = findViewById(R.id.ll_parent);
        ll_parent.setClickable(true);

        tv_timer = findViewById(R.id.tv_timer);
        tv_add = findViewById(R.id.tv_add);
        tv_del = findViewById(R.id.tv_del);
        tv_config = findViewById(R.id.tv_config);
        tv_start = findViewById(R.id.tv_start);

        tv_timer.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        tv_del.setOnClickListener(this);
        tv_config.setOnClickListener(this);
        tv_start.setOnClickListener(this);

        setWindowTouch(ll_parent);
    }

    @Override
    public void onClick(View v) {
        if (v == tv_timer) {
            if (mCallBack != null) {
                mCallBack.addTimer();
            }
        } else if (v == tv_add) {
            if (mCallBack != null) {
                mCallBack.add();
            }
        } else if (v == tv_del) {
            if (mCallBack != null) {
                mCallBack.del();
            }
        } else if (v == tv_config) {
            if (mCallBack != null) {
                mCallBack.config();
            }
        } else if (v == tv_start) {
            if (mCallBack != null) {
                mCallBack.start();
            }
        }
    }

    private CallBack mCallBack;

    public void setCallBack(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public interface CallBack {

        void addTimer();

        void add();

        void del();

        void config();

        void start();
        
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_tool_floating;
    }
}
