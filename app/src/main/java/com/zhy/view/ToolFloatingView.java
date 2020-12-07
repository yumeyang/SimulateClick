package com.zhy.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.zhy.simulate.click.R;


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
    private TextView tv_stop;

    public ToolFloatingView(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_float_tool;
    }

    protected void init() {
        super.init();
        View ll_parent = findViewById(R.id.ll_parent);
        ll_parent.setClickable(true);
        setWindowTouch(ll_parent);

        tv_timer = findViewById(R.id.tv_timer);
        tv_add = findViewById(R.id.tv_add);
        tv_del = findViewById(R.id.tv_del);
        tv_config = findViewById(R.id.tv_config);
        tv_start = findViewById(R.id.tv_start);
        tv_stop = findViewById(R.id.tv_stop);

        tv_timer.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        tv_del.setOnClickListener(this);
        tv_config.setOnClickListener(this);
        tv_start.setOnClickListener(this);
        tv_stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mCallBack == null) {
            return;
        }

        if (v == tv_timer) {

            boolean open = !tv_timer.isSelected();
            tv_timer.setSelected(open);
            if (open) {
                tv_timer.setText("时间（关）");
            } else {
                tv_timer.setText("时间（开）");
            }
            mCallBack.timer(open);
        } else if (v == tv_add) {
            mCallBack.add();
        } else if (v == tv_del) {
            mCallBack.del();
        } else if (v == tv_config) {
            mCallBack.config();
        } else if (v == tv_start) {
            boolean open = !tv_start.isSelected();
            start(open);
            mCallBack.start(open);
        } else if (v == tv_stop) {
            mCallBack.stop();
        }
    }

    public void start(boolean open) {
        tv_start.setSelected(open);
        if (open) {
            tv_start.setText("停止");
        } else {
            tv_start.setText("开始");
        }
    }

    private CallBack mCallBack;

    public void setCallBack(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public interface CallBack {

        void timer(boolean open);

        void add();

        void del();

        void config();

        void start(boolean open);

        void stop();
    }
}
