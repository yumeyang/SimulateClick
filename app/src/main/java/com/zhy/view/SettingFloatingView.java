package com.zhy.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.zhy.simulate.click.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author:yhz
 * @time:2020/12/2
 * @email:309581534@qq.com
 * @describe:
 */
public class SettingFloatingView extends BaseFloatingView {

    private TimePicker tp;
    private TextView tv_save;

    private String mHour;
    private String mMinute;

    @Override
    protected int getLayoutId() {
        return R.layout.view_float_setting;
    }

    public SettingFloatingView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void init() {
        super.init();
        tp = findViewById(R.id.tp);
        tv_save = findViewById(R.id.tv_save);
        tp.setIs24HourView(true);
        tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                mHour = genHour(hourOfDay);
                mMinute = genMinute(minute);
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    if (!TextUtils.isEmpty(mHour) && !TextUtils.isEmpty(mMinute)) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        String time = year + "-" + month + "-" + day + " " + mHour + ":" + mMinute;
                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date date;
                        try {
                            date = formatter.parse(time);
                            mCallBack.save(date.getTime() - 100);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                removeFloatView();
            }
        });
    }

    private CallBack mCallBack;

    public void setCallBack(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public interface CallBack {
        void save(long time);
    }

    public String genMinute(int minute) {
        String minute_;
        if (minute < 10) {
            minute_ = "0" + minute;
        } else {
            minute_ = minute + "";
        }
        return minute_;
    }

    public String genHour(int hour) {
        String hour_;
        if (hour < 10) {
            hour_ = "0" + hour;
        } else {
            hour_ = hour + "";
        }
        return hour_;
    }
}
