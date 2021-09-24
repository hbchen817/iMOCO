package com.laffey.smart.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.cncoderx.wheelview.OnWheelChangedListener;
import com.cncoderx.wheelview.Wheel3DView;
import com.cncoderx.wheelview.WheelView;
import com.google.gson.Gson;
import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityTimeRangeSelectorBinding;
import com.laffey.smart.demoTest.CaConditionEntry;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocalTimeRangeSelectorActivity extends BaseActivity {
    private ActivityTimeRangeSelectorBinding mViewBinding;

    private static final String CRON_TAG = "cron_tag";

    private AlertDialog mBeginDialog;
    private AlertDialog mEndDialog;

    private final List<String> mBeginHourList = new ArrayList<>();
    private final List<String> mBeginMinList = new ArrayList<>();

    private final List<String> mEndHourList = new ArrayList<>();
    private final List<String> mEndMinList = new ArrayList<>();

    private View mBeginView;
    private int mBeginSelectedHour = 8;
    private int mBeginSelectedMin = 0;

    private View mEndView;
    private int mEndSelectedHour = 18;
    private int mEndSelectedMin = 0;

    private Wheel3DView mBeginHourWV;
    private Wheel3DView mBeginMinWV;

    private Wheel3DView mEndHourWV;
    private Wheel3DView mEndMinWV;

    private String mCustomWeekDayResult = null;

    private String mRepeatDays = "";

    private String mCron;

    public static void startForResult(Activity activity, String cron, int requestCode) {
        Intent intent = new Intent(activity, LocalTimeRangeSelectorActivity.class);
        intent.putExtra(CRON_TAG, cron);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityTimeRangeSelectorBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();
        initView();
        initData();
    }

    private void initData() {
        mCron = getIntent().getStringExtra(CRON_TAG);
        if (mCron != null && mCron.length() > 0) {
            if (mCron.contains("0-59 0-23")) {
                // 全天
                mViewBinding.allDaySb.setChecked(true);
            } else {
                // 非全天
                mViewBinding.allDaySb.setChecked(false);
            }
            String[] timers = mCron.split(" ");

            String[] mins = timers[0].split("-");
            String[] hours = timers[1].split("-");
            mBeginSelectedHour = Integer.parseInt(hours[0]);
            mEndSelectedHour = Integer.parseInt(hours[1]);
            mBeginSelectedMin = Integer.parseInt(mins[0]);
            mEndSelectedMin = Integer.parseInt(mins[1]);

            mViewBinding.beginTimeTv.setText(mBeginHourList.get(mBeginSelectedHour) + ":" + mBeginMinList.get(mBeginSelectedMin));
            mViewBinding.endTimeTv.setText(mEndHourList.get(mEndSelectedHour) + ":" + mEndMinList.get(mEndSelectedMin));

            mViewBinding.onceChecked.setVisibility(View.GONE);
            mViewBinding.everydayChecked.setVisibility(View.GONE);
            mViewBinding.workingDaysChecked.setVisibility(View.GONE);
            mViewBinding.weakendChecked.setVisibility(View.GONE);
            mViewBinding.customChecked.setVisibility(View.GONE);

            switch (timers[4]) {
                case "*": {
                    // 每天
                    mRepeatDays = "1,2,3,4,5,6,7";
                    mViewBinding.everydayChecked.setVisibility(View.VISIBLE);
                    break;
                }
                case "MON,TUE,WED,THU,FRI": {
                    // 工作日
                    mRepeatDays = "1,2,3,4,5";
                    mViewBinding.workingDaysChecked.setVisibility(View.VISIBLE);
                    break;
                }
                case "SAT,SUN": {
                    // 周末
                    mRepeatDays = "6,7";
                    mViewBinding.weakendChecked.setVisibility(View.VISIBLE);
                    break;
                }
                default: {
                    // 自定义
                    StringBuilder result = new StringBuilder();
                    String[] days = timers[4].split(",");
                    for (int i = 0; i < days.length; i++) {
                        if ("MON".equals(days[i])) {
                            result.append("1");
                        } else if ("TUE".equals(days[i])) {
                            result.append("2");
                        } else if ("WED".equals(days[i])) {
                            result.append("3");
                        } else if ("THU".equals(days[i])) {
                            result.append("4");
                        } else if ("FRI".equals(days[i])) {
                            result.append("5");
                        } else if ("SAT".equals(days[i])) {
                            result.append("6");
                        } else if ("SUN".equals(days[i])) {
                            result.append("7");
                        }
                        if (i != days.length - 1) {
                            result.append(",");
                        }
                    }
                    mRepeatDays = result.toString();
                    mCustomWeekDayResult = result.toString();
                }
            }
        }
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
        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.onceChecked.setTypeface(iconfont);
        mViewBinding.everydayChecked.setTypeface(iconfont);
        mViewBinding.workingDaysChecked.setTypeface(iconfont);
        mViewBinding.weakendChecked.setTypeface(iconfont);
        mViewBinding.customChecked.setTypeface(iconfont);

        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.time_range));
        mViewBinding.includeToolbar.tvToolbarRight.setText(getString(R.string.nick_name_save));
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder result = new StringBuilder();
                if (mViewBinding.allDaySb.isChecked()) {
                    // 全天
                    result.append("0-59 0-23 * *");
                } else {
                    // 非全天
                    result.append(mBeginSelectedMin + "-" + mEndSelectedMin + " " + mBeginSelectedHour + "-" + mEndSelectedHour + " * *");
                }

                if (mViewBinding.everydayChecked.getVisibility() == View.VISIBLE) {
                    // 每天
                    result.append(" *");
                } else if (mViewBinding.workingDaysChecked.getVisibility() == View.VISIBLE) {
                    // 工作日
                    result.append(" MON,TUE,WED,THU,FRI");
                } else if (mViewBinding.weakendChecked.getVisibility() == View.VISIBLE) {
                    // 周末
                    result.append(" SAT,SUN");
                } else if (mViewBinding.customChecked.getVisibility() == View.VISIBLE) {
                    // 自定义
                    result.append(" ");
                    String[] timers = mRepeatDays.split(",");
                    for (int i = 0; i < timers.length; i++) {
                        switch (timers[i]) {
                            case "1": {
                                result.append("MON");
                                break;
                            }
                            case "2": {
                                result.append("TUE");
                                break;
                            }
                            case "3": {
                                result.append("WED");
                                break;
                            }
                            case "4": {
                                result.append("THU");
                                break;
                            }
                            case "5": {
                                result.append("FRI");
                                break;
                            }
                            case "6": {
                                result.append("SAT");
                                break;
                            }
                            case "7": {
                                result.append("SUN");
                                break;
                            }
                        }
                        if (i != timers.length - 1) {
                            result.append(",");
                        }
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("timer_range_selecter_result", result.toString());
                setResult(0, intent);
                finish();
            }
        });

        mViewBinding.onceLayout.setVisibility(View.GONE);
        mViewBinding.onceChecked.setVisibility(View.GONE);
        mViewBinding.everydayChecked.setVisibility(View.VISIBLE);

        mViewBinding.rangeTimeLayout.setVisibility(mViewBinding.allDaySb.isChecked() ? View.GONE : View.VISIBLE);
        mViewBinding.allDaySb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mViewBinding.rangeTimeLayout.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }
        });

        initDialog();

        mViewBinding.beginTimeLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.endTimeLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.onceLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.everydayLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.workingDaysLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.weekendLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.customLayout.setOnClickListener(this::onViewClicked);
    }

    protected void onViewClicked(View view) {
        if (view.getId() == R.id.begin_time_layout) {
            showBeginDialog();
        } else if (view.getId() == R.id.end_time_layout) {
            showEndDialog();
        } else if (view.getId() == R.id.once_layout) {
            mRepeatDays = "";
            mViewBinding.onceChecked.setVisibility(View.VISIBLE);
            mViewBinding.everydayChecked.setVisibility(View.GONE);
            mViewBinding.workingDaysChecked.setVisibility(View.GONE);
            mViewBinding.weakendChecked.setVisibility(View.GONE);
            mViewBinding.customChecked.setVisibility(View.GONE);
        } else if (view.getId() == R.id.everyday_layout) {
            mRepeatDays = "1,2,3,4,5,6,7";
            mViewBinding.onceChecked.setVisibility(View.GONE);
            mViewBinding.everydayChecked.setVisibility(View.VISIBLE);
            mViewBinding.workingDaysChecked.setVisibility(View.GONE);
            mViewBinding.weakendChecked.setVisibility(View.GONE);
            mViewBinding.customChecked.setVisibility(View.GONE);
        } else if (view.getId() == R.id.working_days_layout) {
            mRepeatDays = "1,2,3,4,5";
            mViewBinding.onceChecked.setVisibility(View.GONE);
            mViewBinding.everydayChecked.setVisibility(View.GONE);
            mViewBinding.workingDaysChecked.setVisibility(View.VISIBLE);
            mViewBinding.weakendChecked.setVisibility(View.GONE);
            mViewBinding.customChecked.setVisibility(View.GONE);
        } else if (view.getId() == R.id.weekend_layout) {
            mRepeatDays = "6,7";
            mViewBinding.onceChecked.setVisibility(View.GONE);
            mViewBinding.everydayChecked.setVisibility(View.GONE);
            mViewBinding.workingDaysChecked.setVisibility(View.GONE);
            mViewBinding.weakendChecked.setVisibility(View.VISIBLE);
            mViewBinding.customChecked.setVisibility(View.GONE);
        } else if (view.getId() == R.id.custom_layout) {
            Intent intent = new Intent(this, TimeRangeRepeatDayActivity.class);
            intent.putExtra("custom_day", mCustomWeekDayResult);
            startActivityForResult(intent, 1000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == 1 && data != null) {
                String result = data.getStringExtra("custom_repeat_result");
                mCustomWeekDayResult = result;
                mRepeatDays = result;

                mViewBinding.onceChecked.setVisibility(View.GONE);
                mViewBinding.everydayChecked.setVisibility(View.GONE);
                mViewBinding.workingDaysChecked.setVisibility(View.GONE);
                mViewBinding.weakendChecked.setVisibility(View.GONE);
                mViewBinding.customChecked.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 时间选项弹出框初始化
     */
    private void initDialog() {
        mBeginDialog = new AlertDialog.Builder(this).create();
        mBeginDialog.setCanceledOnTouchOutside(true);

        mBeginView = LayoutInflater.from(this).inflate(R.layout.custom_begin_time, null, true);
        TextView titleTV = (TextView) mBeginView.findViewById(R.id.dialog_title);
        titleTV.setText(R.string.begin_time);

        mBeginHourWV = (Wheel3DView) mBeginView.findViewById(R.id.time_hour_wv);
        mBeginMinWV = (Wheel3DView) mBeginView.findViewById(R.id.time_min_wv);

        for (int i = 0; i < 24; i++) {
            if (i < 10) mBeginHourList.add("0" + i);
            else mBeginHourList.add(String.valueOf(i));
        }
        for (int i = 0; i < 60; i++) {
            if (i < 10) mBeginMinList.add("0" + i);
            else mBeginMinList.add(String.valueOf(i));
        }

        mBeginHourWV.setEntries(mBeginHourList);
        mBeginMinWV.setEntries(mBeginMinList);

        mBeginHourWV.setCurrentIndex(8);
        mBeginMinWV.setCurrentIndex(0);

        mBeginHourWV.setOnWheelChangedListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView view, int oldIndex, int newIndex) {
                mBeginSelectedHour = newIndex;
            }
        });
        mBeginMinWV.setOnWheelChangedListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView view, int oldIndex, int newIndex) {
                mBeginSelectedMin = newIndex;
            }
        });

        TextView beginCancelTV = (TextView) mBeginView.findViewById(R.id.cancel_tv);
        beginCancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBeginDialog.dismiss();
            }
        });

        TextView beginSureTV = (TextView) mBeginView.findViewById(R.id.sure_tv);
        beginSureTV.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                mViewBinding.beginTimeTv.setText(mBeginHourList.get(mBeginSelectedHour) + ":" + mBeginMinList.get(mBeginSelectedMin));
                mBeginDialog.dismiss();
            }
        });

        // 结束时间
        mEndDialog = new AlertDialog.Builder(this).create();
        mEndDialog.setCanceledOnTouchOutside(true);

        mEndView = LayoutInflater.from(this).inflate(R.layout.custom_end_time, null, true);
        TextView endTitleTV = (TextView) mEndView.findViewById(R.id.dialog_title);
        endTitleTV.setText(R.string.end_time);

        mEndHourWV = (Wheel3DView) mEndView.findViewById(R.id.time_hour_wv);
        mEndMinWV = (Wheel3DView) mEndView.findViewById(R.id.time_min_wv);

        for (int i = 0; i < 24; i++) {
            if (i < 10) mEndHourList.add("0" + i);
            else mEndHourList.add(String.valueOf(i));
        }
        for (int i = 0; i < 60; i++) {
            if (i < 10) mEndMinList.add("0" + i);
            else mEndMinList.add(String.valueOf(i));
        }

        mEndHourWV.setEntries(mEndHourList);
        mEndMinWV.setEntries(mEndMinList);

        mEndHourWV.setCurrentIndex(mEndSelectedHour);
        mEndMinWV.setCurrentIndex(mEndSelectedMin);

        mEndHourWV.setOnWheelChangedListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView view, int oldIndex, int newIndex) {
                mEndSelectedHour = newIndex;
            }
        });
        mEndMinWV.setOnWheelChangedListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView view, int oldIndex, int newIndex) {
                mEndSelectedMin = newIndex;
            }
        });

        TextView endCancelTV = (TextView) mEndView.findViewById(R.id.cancel_tv);
        endCancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEndDialog.dismiss();
            }
        });

        TextView endSureTV = (TextView) mEndView.findViewById(R.id.sure_tv);
        endSureTV.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                mViewBinding.endTimeTv.setText(mEndHourList.get(mEndSelectedHour) + ":" + mEndMinList.get(mEndSelectedMin));
                mEndDialog.dismiss();
            }
        });
    }

    private void showBeginDialog() {
        String beginTime = mViewBinding.beginTimeTv.getText().toString();
        String[] s = beginTime.split(":");
        mBeginHourWV.setCurrentIndex(Integer.parseInt(s[0]));
        mBeginMinWV.setCurrentIndex(Integer.parseInt(s[1]));

        mBeginDialog.show();
        mBeginDialog.setContentView(mBeginView);
    }

    private void showEndDialog() {
        String endTime = mViewBinding.endTimeTv.getText().toString();
        String[] s = endTime.split(":");
        mEndHourWV.setCurrentIndex(Integer.parseInt(s[0]));
        mEndMinWV.setCurrentIndex(Integer.parseInt(s[1]));

        mEndDialog.show();
        mEndDialog.setContentView(mEndView);
    }
}