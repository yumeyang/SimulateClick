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

    private TextView tv_add;
    private TextView tv_del;
    private TextView tv_config;
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

        tv_add = findViewById(R.id.tv_add);
        tv_del = findViewById(R.id.tv_del);
        tv_config = findViewById(R.id.tv_config);
        tv_stop = findViewById(R.id.tv_stop);

        tv_add.setOnClickListener(this);
        tv_del.setOnClickListener(this);
        tv_config.setOnClickListener(this);
        tv_stop.setOnClickListener(this);
    }

    public void setTvStop(int status) {
        if (status == 0) {
            tv_stop.setText("关闭程序");
        } else if (status == 1) {
            tv_stop.setText("停止程序");
        }
        tv_stop.setTag(status);
    }

    @Override
    public void onClick(View v) {
        if (mCallBack == null) {
            return;
        }
        if (v == tv_add) {
            mCallBack.add();
        } else if (v == tv_del) {
            mCallBack.del();
        } else if (v == tv_config) {
            mCallBack.config();
        } else if (v == tv_stop) {
            mCallBack.stop((int) tv_stop.getTag());
        }
    }

    private CallBack mCallBack;

    public void setCallBack(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public interface CallBack {

        void add();

        void del();

        void config();

        void stop(int status);
    }
}
