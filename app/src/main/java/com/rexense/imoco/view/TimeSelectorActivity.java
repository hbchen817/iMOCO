package com.rexense.imoco.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.cncoderx.wheelview.OnWheelChangedListener;
import com.cncoderx.wheelview.Wheel3DView;
import com.cncoderx.wheelview.WheelView;
import com.google.gson.Gson;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.demoTest.CaConditionEntry;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimeSelectorActivity extends BaseActivity {
    @BindView(R.id.tv_toolbar_title)
    TextView mTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.time_hour_wv)
    Wheel3DView mTimeHourWV;
    @BindView(R.id.time_min_wv)
    Wheel3DView mTimeMinWV;
    @BindView(R.id.once_layout)
    RelativeLayout mOnceLayout;
    @BindView(R.id.once_checked)
    TextView mOnceIV;
    @BindView(R.id.everyday_layout)
    RelativeLayout mEverydayLayout;
    @BindView(R.id.everyday_checked)
    TextView mEverydayIV;
    @BindView(R.id.working_days_layout)
    RelativeLayout mWorkingDaysLayout;
    @BindView(R.id.working_days_checked)
    TextView mWorkingDaysIV;
    @BindView(R.id.weekend_layout)
    RelativeLayout mWeekendLayout;
    @BindView(R.id.weakend_checked)
    TextView mWeakendIV;
    @BindView(R.id.custom_layout)
    RelativeLayout mCustomLayout;
    @BindView(R.id.custom_checked)
    TextView mCustomIV;

    private List<String> mHourList = new ArrayList<>();
    private List<String> mMinList = new ArrayList<>();

    private String mSelectHour;
    private String mSelectMin;
    private String mTimeResult;
    private String mCustomWeekDayResult = null;

    private CaConditionEntry.Timer mTimerEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_selector);
        ButterKnife.bind(this);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/jk/iconfont.ttf");
        mOnceIV.setTypeface(iconfont);
        mEverydayIV.setTypeface(iconfont);
        mWorkingDaysIV.setTypeface(iconfont);
        mWeakendIV.setTypeface(iconfont);
        mCustomIV.setTypeface(iconfont);

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
        mTitle.setText(getString(R.string.timer_point));
        tvToolbarRight.setText(getString(R.string.nick_name_save));

        for (int i = 0; i < 24; i++) {
            if (i < 10) mHourList.add("0" + i);
            else mHourList.add(String.valueOf(i));
        }
        for (int i = 0; i < 60; i++) {
            if (i < 10) mMinList.add("0" + i);
            else mMinList.add(String.valueOf(i));
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        int hour = Integer.valueOf(time.split(":")[3]);
        int min = Integer.valueOf(time.split(":")[4]);

        mSelectHour = time.split(":")[3];
        mSelectMin = time.split(":")[4];

        mTimeHourWV.setEntries(mHourList);
        mTimeHourWV.setCurrentIndex(hour);
        mTimeHourWV.setOnWheelChangedListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView view, int oldIndex, int newIndex) {
                mSelectHour = mHourList.get(newIndex);
            }
        });

        mTimeMinWV.setEntries(mMinList);
        mTimeMinWV.setCurrentIndex(min);
        mTimeMinWV.setOnWheelChangedListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView view, int oldIndex, int newIndex) {
                mSelectMin = mMinList.get(newIndex);
            }
        });

        mTimerEntry = new CaConditionEntry.Timer();
    }

    @OnClick({R.id.tv_toolbar_right, R.id.once_layout, R.id.everyday_layout, R.id.working_days_layout,
            R.id.weekend_layout, R.id.custom_layout})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.custom_layout: {
                Intent intent = new Intent(this, CustomRepeatDayActivity.class);
                intent.putExtra("custom_day", mCustomWeekDayResult);
                startActivityForResult(intent, 1000);
                break;
            }
            case R.id.weekend_layout: {
                mCustomWeekDayResult = null;
                mOnceIV.setVisibility(View.GONE);
                mEverydayIV.setVisibility(View.GONE);
                mWorkingDaysIV.setVisibility(View.GONE);
                mWeakendIV.setVisibility(View.VISIBLE);
                mCustomIV.setVisibility(View.GONE);
                break;
            }
            case R.id.working_days_layout: {
                mCustomWeekDayResult = null;
                mOnceIV.setVisibility(View.GONE);
                mEverydayIV.setVisibility(View.GONE);
                mWorkingDaysIV.setVisibility(View.VISIBLE);
                mWeakendIV.setVisibility(View.GONE);
                mCustomIV.setVisibility(View.GONE);
                break;
            }
            case R.id.everyday_layout: {
                mCustomWeekDayResult = null;
                mOnceIV.setVisibility(View.GONE);
                mEverydayIV.setVisibility(View.VISIBLE);
                mWorkingDaysIV.setVisibility(View.GONE);
                mWeakendIV.setVisibility(View.GONE);
                mCustomIV.setVisibility(View.GONE);
                break;
            }
            case R.id.once_layout: {
                mCustomWeekDayResult = null;
                mOnceIV.setVisibility(View.VISIBLE);
                mEverydayIV.setVisibility(View.GONE);
                mWorkingDaysIV.setVisibility(View.GONE);
                mWeakendIV.setVisibility(View.GONE);
                mCustomIV.setVisibility(View.GONE);
                break;
            }
            case R.id.tv_toolbar_right: {
                if (mOnceIV.getVisibility() == View.VISIBLE) {
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd:HH:mm");
                        Date date = new Date(System.currentTimeMillis());
                        String[] time = format.format(date).split(":");
                        int hour = Integer.valueOf(time[3]);
                        int min = Integer.valueOf(time[4]);

                        if (Integer.valueOf(mSelectHour) < hour
                                || (Integer.valueOf(mSelectHour) == hour && Integer.valueOf(mSelectMin) <= min)) {
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
                } else if (mEverydayIV.getVisibility() == View.VISIBLE) {
                    // 每天
                    mTimeResult = "0 " + Integer.valueOf(mSelectMin) + " " + Integer.valueOf(mSelectHour) + " * * ? *";
                    mTimerEntry.setCron(mTimeResult);
                    mTimerEntry.setCronType(Constant.TIMER_QUARTZ_CRON);
                    mTimerEntry.setTimezoneID(Constant.TIMER_ZONE_ID);
                } else if (mWorkingDaysIV.getVisibility() == View.VISIBLE) {
                    // 工作日
                    mTimeResult = "0 " + Integer.valueOf(mSelectMin) + " " + Integer.valueOf(mSelectHour) + " ? * mon,tue,wed,thu,fri *";
                    mTimerEntry.setCron(mTimeResult);
                    mTimerEntry.setCronType(Constant.TIMER_QUARTZ_CRON);
                    mTimerEntry.setTimezoneID(Constant.TIMER_ZONE_ID);
                } else if (mWeakendIV.getVisibility() == View.VISIBLE) {
                    // 周末
                    mTimeResult = "0 " + Integer.valueOf(mSelectMin) + " " + Integer.valueOf(mSelectHour) + " ? * sat,sun *";
                    mTimerEntry.setCron(mTimeResult);
                    mTimerEntry.setCronType(Constant.TIMER_QUARTZ_CRON);
                    mTimerEntry.setTimezoneID(Constant.TIMER_ZONE_ID);
                } else if (mCustomIV.getVisibility() == View.VISIBLE) {
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
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1000: {
                if (resultCode == 1) {
                    String result = data.getStringExtra("custom_repeat_result");
                    mCustomWeekDayResult = result;

                    mOnceIV.setVisibility(View.GONE);
                    mEverydayIV.setVisibility(View.GONE);
                    mWorkingDaysIV.setVisibility(View.GONE);
                    mWeakendIV.setVisibility(View.GONE);
                    mCustomIV.setVisibility(View.VISIBLE);
                }
                break;
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
                hour = Integer.valueOf(mSelectHour);
                min = Integer.valueOf(mSelectMin);
            } else if (Constant.TIMER_QUARTZ_CRON.equals(mTimerEntry.getCronType())) {
                String[] s = mTimerEntry.getCron().split(" ");
                mSelectHour = s[2];
                mSelectMin = s[1];
                hour = Integer.valueOf(mSelectHour);
                min = Integer.valueOf(mSelectMin);
            }

            mTimeHourWV.setCurrentIndex(hour);
            mTimeMinWV.setCurrentIndex(min);

            mOnceIV.setVisibility(View.GONE);
            mEverydayIV.setVisibility(View.GONE);
            mWorkingDaysIV.setVisibility(View.GONE);
            mWeakendIV.setVisibility(View.GONE);
            mCustomIV.setVisibility(View.GONE);

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
                mOnceIV.setVisibility(View.VISIBLE);
            } else if ("?".equals(crons[crons.length - 2])) {
                // 每天
                mEverydayIV.setVisibility(View.VISIBLE);
            } else if ("mon,tue,wed,thu,fri".equals(crons[crons.length - 2])) {
                // 工作日
                mWorkingDaysIV.setVisibility(View.VISIBLE);
            } else if ("sat,sun".equals(crons[crons.length - 2])) {
                // 周末
                mWeakendIV.setVisibility(View.VISIBLE);
            } else {
                // 自定义
                mCustomIV.setVisibility(View.VISIBLE);
                mCustomWeekDayResult = crons[crons.length - 2];
            }

            EventBus.getDefault().removeStickyEvent(message);
        }
    }
}