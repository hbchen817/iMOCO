package com.laffey.smart.view;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.cncoderx.wheelview.OnWheelChangedListener;
import com.cncoderx.wheelview.Wheel3DView;
import com.cncoderx.wheelview.WheelView;
import com.google.gson.Gson;
import com.kyleduo.switchbutton.SwitchButton;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimeRangeSelectorActivity extends BaseActivity {
    private ActivityTimeRangeSelectorBinding mViewBinding;

    private AlertDialog mBeginDialog;
    private AlertDialog mEndDialog;

    private List<String> mBeginHourList = new ArrayList<>();
    private List<String> mBeginMinList = new ArrayList<>();

    private List<String> mEndHourList = new ArrayList<>();
    private List<String> mEndMinList = new ArrayList<>();

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

    private CaConditionEntry.TimeRange mTimeRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityTimeRangeSelectorBinding.inflate(getLayoutInflater());
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
        mViewBinding.includeToolbar.tvToolbarTitle.setText(getString(R.string.time_range));
        mViewBinding.includeToolbar.tvToolbarRight.setText(getString(R.string.nick_name_save));
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimeRange.setTimezoneID(Constant.TIMER_ZONE_ID);
                if (mViewBinding.allDaySb.isChecked()) {
                    mTimeRange.setFormat("HH:mm:ss");
                    mTimeRange.setBeginDate("00:00:00");
                    mTimeRange.setEndDate("23:59:59");
                } else {
                    mTimeRange.setFormat("HH:mm:ss");
                    mTimeRange.setBeginDate(mViewBinding.beginTimeTv.getText().toString() + ":00");
                    mTimeRange.setEndDate(mViewBinding.endTimeTv.getText().toString() + ":00");
                }
                if (mViewBinding.onceChecked.getVisibility() == View.VISIBLE) {
                    if (!mViewBinding.allDaySb.isChecked()) {
                        try {
                            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String s = format2.format(new Date(System.currentTimeMillis()));

                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            long i = format.parse(s + " " + mViewBinding.beginTimeTv.getText().toString()).getTime();
                            long j = format.parse(s + " " + mViewBinding.endTimeTv.getText().toString()).getTime();
                            long n = System.currentTimeMillis();
                            if (n >= j && j >= i) {
                                ToastUtils.showLongToast(TimeRangeSelectorActivity.this, R.string.invalid_for_past_time_period);
                                return;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    mTimeRange.setRepeat(mRepeatDays);
                }

                EventBus.getDefault().postSticky(mTimeRange);

                Intent intent = new Intent(TimeRangeSelectorActivity.this, NewSceneActivity.class);
                startActivity(intent);
            }
        });

        mViewBinding.rangeTimeLayout.setVisibility(mViewBinding.allDaySb.isChecked() ? View.GONE : View.VISIBLE);
        mViewBinding.allDaySb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mViewBinding.rangeTimeLayout.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }
        });

        initDialog();
        mTimeRange = new CaConditionEntry.TimeRange();

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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(Message message) {
        if (message.what == Constant.SCENE_CONDITION_TIME_RANGE_EDIT) {
            ViseLog.d(new Gson().toJson(message.obj));
            mTimeRange = (CaConditionEntry.TimeRange) message.obj;
            if ("00:00:00".equals(mTimeRange.getBeginDate()) && "23:59:59".equals(mTimeRange.getEndDate())) {
                mViewBinding.allDaySb.setChecked(true);
            } else {
                mViewBinding.allDaySb.setChecked(false);
                String beginDate = mTimeRange.getBeginDate();
                beginDate = beginDate.substring(0, beginDate.lastIndexOf(":00"));
                String endDate = mTimeRange.getEndDate();
                endDate = endDate.substring(0, endDate.lastIndexOf(":00"));
                mViewBinding.beginTimeTv.setText(beginDate);
                mViewBinding.endTimeTv.setText(endDate);
            }
            mViewBinding.onceChecked.setVisibility(View.GONE);
            mViewBinding.everydayChecked.setVisibility(View.GONE);
            mViewBinding.workingDaysChecked.setVisibility(View.GONE);
            mViewBinding.weakendChecked.setVisibility(View.GONE);
            mViewBinding.customChecked.setVisibility(View.GONE);

            String repeat = mTimeRange.getRepeat();
            mRepeatDays = repeat;
            if (repeat == null || repeat.length() == 0) {
                mViewBinding.onceChecked.setVisibility(View.VISIBLE);
            } else if ("1,2,3,4,5,6,7".equals(repeat)) {
                mViewBinding.everydayChecked.setVisibility(View.VISIBLE);
            } else if ("1,2,3,4,5".equals(repeat)) {
                mViewBinding.workingDaysChecked.setVisibility(View.VISIBLE);
            } else if ("6,7".equals(repeat)) {
                mViewBinding.weakendChecked.setVisibility(View.VISIBLE);
            } else {
                mViewBinding.customChecked.setVisibility(View.VISIBLE);
                mCustomWeekDayResult = repeat;
            }

            EventBus.getDefault().removeStickyEvent(message);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}