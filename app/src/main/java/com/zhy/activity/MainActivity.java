package com.zhy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.zhy.service.FloatingViewService;
import com.zhy.view.androidsimulateclickdemon.R;

public class MainActivity extends Activity implements View.OnClickListener
{

    private TextView btn_start_service;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_main);
        btn_start_service = findViewById(R.id.btn_start_service);
        btn_start_service.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v == btn_start_service)
        {
            if (!FloatingViewService.isStart())
            {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                FloatingViewService.start(this);
            } else
            {
                FloatingViewService.start(this);
            }
        }
    }
}