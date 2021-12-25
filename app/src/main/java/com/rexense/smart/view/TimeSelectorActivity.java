package com.rexense.smart.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import androidx.annotation.Nullable;

import com.cncoderx.wheelview.OnWheelChangedListener;
import com.cncoderx.wheelview.WheelView;
import com.google.gson.Gson;
import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityTimeSelectorBinding;
import com.rexense.smart.demoTest.CaConditionEntry;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeSelectorActivity extends BaseActivity {
    private ActivityTimeSelectorBinding mViewBinding;

    private List<String> mHourList = new ArrayList<>();
    private List<String> mMinList = new ArrayList<>();

    private String mSelectHour;
    private String mSelectMin;
    private String mCustomWeekDayResult = null;

    private CaConditionEntry.Timer mTimerEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityTimeSelectorBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.onceChecked.setTypeface(iconfont);
        mViewBinding.everydayChecked.setTypeface(iconfont);
        mViewBinding.workingDaysChecked.setTypeface(iconfont);
        mViewBinding.weakendChecked.setTypeface(iconfont);
        mViewBinding.customChecked.setTypeface(iconfont);

        initStatusBar();

        initView();

        EventBus.getDefault().register(this);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    private void initView() {
        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.timer_point));
        mViewBinding.includeToolbar.tvToolbarRight.setText(getString(R.string.nick_name_save));

        for (int i = 0; i < 24; i++) {
            if (i < 10) mHourList.add("0" + i);
            else mHourList.add(String.valueOf(i));
        }
        for (int i = 0; i < 60; i++) {
            if (i < 10) mMinList.add("0" + i);
            else mMinList.add(String.valueOf(i));
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss", Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        int hour = Integer.parseInt(time.split(":")[3]);
        int min = Integer.parseInt(time.split(":")[4]);

        mSelectHour = time.split(":")[3];
        mSelectMin = time.split(":")[4];

        mViewBinding.timeHourWv.setEntries(mHourList);
        mViewBinding.timeHourWv.setCurrentIndex(hour);
        mViewBinding.timeHourWv.setOnWheelChangedListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView view, int oldIndex, int newIndex) {
                mSelectHour = mHourList.get(newIndex);
            }
        });

        mViewBinding.timeMinWv.setEntries(mMinList);
        mViewBinding.timeMinWv.setCurrentIndex(min);
        mViewBinding.timeMinWv.setOnWheelChangedListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView view, int oldIndex, int newIndex) {
                mSelectMin = mMinList.get(newIndex);
            }
        });

        mTimerEntry = new CaConditionEntry.Timer();

        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this::onViewClicked);
        mViewBinding.onceLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.everydayLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.workingDaysLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.weekendLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.customLayout.setOnClickListener(this::onViewClicked);
    }

    protected void onViewClicked(View view) {
        if (view.getId() == R.id.custom_layout) {
            Intent intent = new Intent(this, CustomRepeatDayActivity.class);
            intent.putExtra("custom_day", mCustomWeekDayResult);
            startActivityForResult(intent, 1000);
        } else if (view.getId() == R.id.weekend_layout) {
            mCustomWeekDayResult = null;
            mViewBinding.onceChecked.setVisibility(View.GONE);
            mViewBinding.everydayChecked.setVisibility(View.GONE);
            mViewBinding.workingDaysChecked.setVisibility(View.GONE);
            mViewBinding.weakendChecked.setVisibility(View.VISIBLE);
            mViewBinding.customChecked.setVisibility(View.GONE);
        } else if (view.getId() == R.id.working_days_layout) {
            mCustomWeekDayResult = null;
            mViewBinding.onceChecked.setVisibility(View.GONE);
            mViewBinding.everydayChecked.setVisibility(View.GONE);
            mViewBinding.workingDaysChecked.setVisibility(View.VISIBLE);
            mViewBinding.weakendChecked.setVisibility(View.GONE);
            mViewBinding.customChecked.setVisibility(View.GONE);
        } else if (view.getId() == R.id.everyday_layout) {
            mCustomWeekDayResult = null;
            mViewBinding.onceChecked.setVisibility(View.GONE);
            mViewBinding.everydayChecked.setVisibility(View.VISIBLE);
            mViewBinding.workingDaysChecked.setVisibility(View.GONE);
            mViewBinding.weakendChecked.setVisibility(View.GONE);
            mViewBinding.customChecked.setVisibility(View.GONE);
        } else if (view.getId() == R.id.once_layout) {
            mCustomWeekDayResult = null;
            mViewBinding.onceChecked.setVisibility(View.VISIBLE);
            mViewBinding.everydayChecked.setVisibility(View.GONE);
            mViewBinding.workingDaysChecked.setVisibility(View.GONE);
            mViewBinding.weakendChecked.setVisibility(View.GONE);
            mViewBinding.customChecked.setVisibility(View.GONE);
        } else if (view.getId() == R.id.tv_toolbar_right) {
            String mTimeResult;
            if (mViewBinding.onceChecked.getVisibility() == View.VISIBLE) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd:HH:mm", Locale.getDefault());
                    Date date = new Date(System.currentTimeMillis());
                    String[] time = format.format(date).split(":");
                    int hour = Integer.parseInt(time[3]);
                    int min = Integer.parseInt(time[4]);

                    if (Integer.parseInt(mSelectHour) < hour
                            || (Integer.parseInt(mSelectHour) == hour && Integer.parseInt(mSelectMin) <= min)) {
                        // 如果选择的时间小于等于现在的时间，就往后推一天
                        date = format.parse(time[0] + ":" + time[1] + ":" + time[2] + ":" + mSelectHour + ":" + mSelectMin);
                        long timelong = date.getTime();
                        timelong = timelong + 24 * 60 * 60 * 1000;
                        String[] results = format.format(timelong).split(":");
                        mTimeResult = "0 " + Integer.valueOf(results[4]) + " " + Integer.valueOf(results[3]) + " " + Integer.valueOf(results[2])
                                + " " + Integer.valueOf(results[1]) + " ? " + Integer.valueOf(results[0]);
                    } else {
                        mTimeResult = "0 " + Integer.valueOf(mSelectMin) + " " + Integer.valueOf(mSelectHour) + " " + Integer.valueOf(time[2])
                                + " " + Integer.valueOf(time[1]) + " ? " + Integer.valueOf(time[0]);
                    }
                    mTimerEntry.setCron(mTimeResult);
                    mTimerEntry.setCronType(Constant.TIMER_QUARTZ_CRON);
                    mTimerEntry.setTimezoneID(Constant.TIMER_ZONE_ID);
                } catch (Exception e) {
                    ViseLog.e(e.toString());
                    e.printStackTrace();
                }
            } else if (mViewBinding.everydayChecked.getVisibility() == View.VISIBLE) {
                // 每天
                mTimeResult = "0 " + Integer.valueOf(mSelectMin) + " " + Integer.valueOf(mSelectHour) + " * * ? *";
                mTimerEntry.setCron(mTimeResult);
                mTimerEntry.setCronType(Constant.TIMER_QUARTZ_CRON);
                mTimerEntry.setTimezoneID(Constant.TIMER_ZONE_ID);
            } else if (mViewBinding.workingDaysChecked.getVisibility() == View.VISIBLE) {
                // 工作日
                mTimeResult = "0 " + Integer.valueOf(mSelectMin) + " " + Integer.valueOf(mSelectHour) + " ? * mon,tue,wed,thu,fri *";
                mTimerEntry.setCron(mTimeResult);
                mTimerEntry.setCronType(Constant.TIMER_QUARTZ_CRON);
                mTimerEntry.setTimezoneID(Constant.TIMER_ZONE_ID);
            } else if (mViewBinding.weakendChecked.getVisibility() == View.VISIBLE) {
                // 周末
                mTimeResult = "0 " + Integer.valueOf(mSelectMin) + " " + Integer.valueOf(mSelectHour) + " ? * sat,sun *";
                mTimerEntry.setCron(mTimeResult);
                mTimerEntry.setCronType(Constant.TIMER_QUARTZ_CRON);
                mTimerEntry.setTimezoneID(Constant.TIMER_ZONE_ID);
            } else if (mViewBinding.customChecked.getVisibility() == View.VISIBLE) {
                // 自定义
                mTimeResult = "0 " + Integer.valueOf(mSelectMin) + " " + Integer.valueOf(mSelectHour) + " ? * " + mCustomWeekDayResult + " *";
                mTimerEntry.setCron(mTimeResult);
                mTimerEntry.setCronType(Constant.TIMER_QUARTZ_CRON);
                mTimerEntry.setTimezoneID(Constant.TIMER_ZONE_ID);
            }
            ViseLog.d(new Gson().toJson(mTimerEntry));

            EventBus.getDefault().postSticky(mTimerEntry);

            Intent intent = new Intent(this, NewSceneActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == 1 && data != null) {
                mCustomWeekDayResult = data.getStringExtra("custom_repeat_result");

                mViewBinding.onceChecked.setVisibility(View.GONE);
                mViewBinding.everydayChecked.setVisibility(View.GONE);
                mViewBinding.workingDaysChecked.setVisibility(View.GONE);
                mViewBinding.weakendChecked.setVisibility(View.GONE);
                mViewBinding.customChecked.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(Message message) {
        if (message.what == Constant.SCENE_CONDITION_TIMER_EDIT) {
            mTimerEntry = (CaConditionEntry.Timer) message.obj;
            int hour = 0;
            int min = 0;
            if (Constant.TIMER_LINUX.equals(mTimerEntry.getCronType())) {
                String[] s = mTimerEntry.getCron().split(" ");
                mSelectHour = s[1];
                mSelectMin = s[0];
                hour = Integer.parseInt(mSelectHour);
                min = Integer.parseInt(mSelectMin);
            } else if (Constant.TIMER_QUARTZ_CRON.equals(mTimerEntry.getCronType())) {
                String[] s = mTimerEntry.getCron().split(" ");
                mSelectHour = s[2];
                mSelectMin = s[1];
                hour = Integer.parseInt(mSelectHour);
                min = Integer.parseInt(mSelectMin);
            }

            mViewBinding.timeHourWv.setCurrentIndex(hour);
            mViewBinding.timeMinWv.setCurrentIndex(min);

            mViewBinding.onceChecked.setVisibility(View.GONE);
            mViewBinding.everydayChecked.setVisibility(View.GONE);
            mViewBinding.workingDaysChecked.setVisibility(View.GONE);
            mViewBinding.weakendChecked.setVisibility(View.GONE);
            mViewBinding.customChecked.setVisibility(View.GONE);

            /*if (Constant.TIMER_QUARTZ_CRON.equals(mTimerEntry.getCronType())) {
                mOnceIV.setVisibility(View.VISIBLE);
            } else {
                String[] s = mTimerEntry.getCron().split(" ");
                if ("*".equals(s[s.length - 1])) {
                    mEverydayIV.setVisibility(View.VISIBLE);
                } else if ("1,2,3,4,5".equals(s[s.length - 1])) {
                    mWorkingDaysIV.setVisibility(View.VISIBLE);
                } else if ("6,7".equals(s[s.length - 1])) {
                    mWeakendIV.setVisibility(View.VISIBLE);
                } else {
                    mCustomIV.setVisibility(View.VISIBLE);
                    mCustomWeekDayResult = s[s.length - 1];
                }
            }*/

            String[] crons = mTimerEntry.getCron().split(" ");
            if (!"*".equals(crons[crons.length - 1])) {
                // 执行一次
                mViewBinding.onceChecked.setVisibility(View.VISIBLE);
            } else if ("?".equals(crons[crons.length - 2])) {
                // 每天
                mViewBinding.everydayChecked.setVisibility(View.VISIBLE);
            } else if ("mon,tue,wed,thu,fri".equals(crons[crons.length - 2])) {
                // 工作日
                mViewBinding.workingDaysChecked.setVisibility(View.VISIBLE);
            } else if ("sat,sun".equals(crons[crons.length - 2])) {
                // 周末
                mViewBinding.weakendChecked.setVisibility(View.VISIBLE);
            } else {
                // 自定义
                mViewBinding.customChecked.setVisibility(View.VISIBLE);
                mCustomWeekDayResult = crons[crons.length - 2];
            }

            EventBus.getDefault().removeStickyEvent(message);
        }
    }
}