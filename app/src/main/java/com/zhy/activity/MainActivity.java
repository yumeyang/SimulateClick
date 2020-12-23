package com.zhy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.RequestExecutor;
import com.zhy.OverlayRationale;
import com.zhy.service.FloatingService;
import com.zhy.simulate.click.R;
import com.zhy.utils.AccessibilityUtil;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends Activity implements View.OnClickListener {

    public static List<String> mList = new ArrayList<>();

    static {
        mList.clear();
        for (int i = 0; i < 1000; i++) {
            mList.add(String.valueOf(i));
        }
    }

    private TextView btn_float_open;
    private TextView btn_access_open;
    private TextView btn_start_service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        initView();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView() {
        btn_float_open = findViewById(R.id.btn_float_open);
        btn_access_open = findViewById(R.id.btn_access_open);
        btn_start_service = findViewById(R.id.btn_start_service);

        btn_float_open.setOnClickListener(this);
        btn_access_open.setOnClickListener(this);
        btn_start_service.setOnClickListener(this);
    }

    private void initData() {
        boolean is_access = AccessibilityUtil.isAccessibilitySettingsOn(this);
        if (is_access) {
            btn_access_open.setText("已开启");
            btn_access_open.setClickable(false);
            btn_access_open.setSelected(true);
        } else {
            btn_access_open.setText("去开启");
            btn_access_open.setClickable(true);
            btn_access_open.setSelected(false);
        }

        AndPermission.with(this).overlay().rationale(new OverlayRationale() {
            @Override
            public void showRationale(Context context, Void data, RequestExecutor executor) {
                setBtnFloatOpen(false);
            }
        }).onGranted(new Action<Void>() {
            @Override
            public void onAction(Void data) {
                setBtnFloatOpen(true);
            }
        }).start();
    }

    private void setBtnFloatOpen(boolean open) {
        if (open) {
            btn_float_open.setText("已开启");
            btn_float_open.setClickable(false);
            btn_float_open.setSelected(true);
        } else {
            btn_float_open.setText("去开启");
            btn_float_open.setClickable(true);
            btn_float_open.setSelected(false);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_start_service) {
            boolean float_open = btn_float_open.isSelected();
            if (!float_open) {
                Toast.makeText(this, "悬浮权限未开启", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean access_open = btn_access_open.isSelected();
            if (!access_open) {
                Toast.makeText(this, "辅助设置未开启", Toast.LENGTH_SHORT).show();
                return;
            }
            FloatingService.addToolFloating(this);
        } else if (v == btn_access_open) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        } else if (v == btn_float_open) {
            AndPermission.with(this).overlay().rationale(new OverlayRationale()).onGranted(new Action<Void>() {
                @Override
                public void onAction(Void data) {
                    Toast.makeText(MainActivity.this, "已开启悬浮权限", Toast.LENGTH_SHORT).show();
                    setBtnFloatOpen(true);
                }
            }).onDenied(new Action<Void>() {
                @Override
                public void onAction(Void data) {
                    Toast.makeText(MainActivity.this, "已拒绝开启悬浮权限", Toast.LENGTH_SHORT).show();
                    setBtnFloatOpen(false);
                }
            }).start();
        }
    }
}