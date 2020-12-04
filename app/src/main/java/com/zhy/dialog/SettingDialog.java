package com.zhy.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zhy.view.androidsimulateclickdemon.R;

/**
 * @author:yhz
 * @time:2020/12/2
 * @email:309581534@qq.com
 * @describe:
 */
public class SettingDialog extends Dialog {

    private EditText et_time;
    private TextView tv_save;

    public SettingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_setting);

        init();
    }

    private void init() {
        et_time = findViewById(R.id.et_time);
        tv_save = findViewById(R.id.tv_save);

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    mCallBack.save();
                }
                dismiss();
            }
        });
    }

    private CallBack mCallBack;

    public void setCallBack(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public interface CallBack {
        void save();
    }
}
