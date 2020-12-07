package com.zhy.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.weigan.loopview.LoopView;
import com.zhy.activity.MainActivity;
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
    private LoopView loop_view;
    private TextView tv_dismiss;
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
        initView();
        initListener();
        initData();
    }

    private void initView() {
        tp = findViewById(R.id.tp);
        loop_view = findViewById(R.id.loop_view);
        tv_dismiss = findViewById(R.id.tv_dismiss);
        tv_save = findViewById(R.id.tv_save);
        tp.setIs24HourView(true);
    }

    private void initListener() {

        tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                mHour = genHour(hourOfDay);
                mMinute = genMinute(minute);
            }
        });

        tv_dismiss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFloatView();
            }
        });

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack == null) {
                    return;
                }

                long long_sub_time = 0;
                String sub_time = MainActivity.mList.get(loop_view.getSelectedItem());
                if (!TextUtils.isEmpty(sub_time)) {
                    long_sub_time = Integer.parseInt(sub_time);
                }

                if (long_sub_time >= 1000) {
                    Toast.makeText(v.getContext(), "只能设置小于1000毫秒", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!TextUtils.isEmpty(mHour) && !TextUtils.isEmpty(mMinute)) {
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    String time = year + "-" + month + "-" + day + " " + mHour + ":" + mMinute;

                    @SuppressLint("SimpleDateFormat")
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        Date date = formatter.parse(time);
                        if (date != null) {
                            long save_time = date.getTime() - long_sub_time;
                            mCallBack.save(save_time);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                removeFloatView();
            }
        });
    }

    private void initData() {
        loop_view.setItems(MainActivity.mList);
        loop_view.setInitPosition(100);
    }

    private CallBack mCallBack;

    public void setCallBack(CallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public interface CallBack {
        void save(long time);
    }

    private String genMinute(int minute) {
        String minute_;
        if (minute < 10) {
            minute_ = "0" + minute;
        } else {
            minute_ = minute + "";
        }
        return minute_;
    }

    private String genHour(int hour) {
        String hour_;
        if (hour < 10) {
            hour_ = "0" + hour;
        } else {
            hour_ = hour + "";
        }
        return hour_;
    }
}
