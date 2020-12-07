package com.zhy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

/**
 * Created by Zhenjie Yan on 2018/5/30.
 */
public class OverlayRationale implements Rationale<Void> {

    @Override
    public void showRationale(Context context, Void data, final RequestExecutor executor) {
        new AlertDialog.Builder(context).setCancelable(false)
                .setTitle("提示")
                .setMessage("请允许开启悬浮权限")
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executor.execute();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executor.cancel();
                    }
                })
                .show();
    }
}