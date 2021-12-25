package com.rexense.smart.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;

import com.aigestudio.wheelpicker.WheelPicker;
import com.rexense.smart.R;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivitySetTimeBinding;
import com.rexense.smart.model.EChoice;
import com.rexense.smart.model.EScene;

/**
 * Creator: xieshaobing
 * creat time: 2020-06-11 17:14
 * Description: 设置时间界面
 */
public class SetTimeActivity extends Activity {
    private ActivitySetTimeBinding mViewBinding;

    private EScene.conditionTimeEntry mConditionTime;
    private String mFirstValue, mSecondValue;
    private int mType;

    // 设置滑轮选择器
    @SuppressLint("SetTextI18n")
    private void setWheelPicker(int type, String firstInitValue, String secondInitValue) {
        mType = type;
        mViewBinding.includeWheelPicker.twoItemWheelPickerLblValue.setText(firstInitValue + " : " + secondInitValue);
        mFirstValue = firstInitValue;
        mSecondValue = secondInitValue;
        mViewBinding.includeWheelPicker.twoItemWheelPickerLblOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewBinding.includeWheelPicker.twoItemWheelPickerRLPicker.setVisibility(View.GONE);
                if (mType == 1) {
                    mViewBinding.setTimeLblBegin.setText(mFirstValue + ":" + mSecondValue);
                    mConditionTime.beginHour = Integer.parseInt(mFirstValue);
                    mConditionTime.beginMinute = Integer.parseInt(mSecondValue);
                } else {
                    mViewBinding.setTimeLblEnd.setText(mFirstValue + ":" + mSecondValue);
                    mConditionTime.endHour = Integer.parseInt(mFirstValue);
                    mConditionTime.endMinute = Integer.parseInt(mSecondValue);
                }
                mViewBinding.setTimeSwtAllday.setChecked(mConditionTime.isAllDay());
            }
        });
        mViewBinding.includeWheelPicker.twoItemWheelPickerLblCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewBinding.includeWheelPicker.twoItemWheelPickerRLPicker.setVisibility(View.GONE);
            }
        });

        // 第一个滚轮处理
        List<String> firstItems = new ArrayList<String>();
        int selectedIndex = 0;
        for (int i = 0; i < 24; i++) {
            firstItems.add(String.format(Locale.getDefault(), "%02d", i));
            if (firstInitValue.equals(firstItems.get(i))) {
                selectedIndex = i;
            }
        }
        mViewBinding.includeWheelPicker.firstItemWheelPickerWPPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                mFirstValue = data.toString();
                mViewBinding.includeWheelPicker.twoItemWheelPickerLblValue.setText(mFirstValue + ":" + mSecondValue);
            }
        });
        // 加载两次数据是为了正确初始选中位置
        for (int i = 0; i < 2; i++) {
            mViewBinding.includeWheelPicker.firstItemWheelPickerWPPicker.setData(firstItems);
            mViewBinding.includeWheelPicker.firstItemWheelPickerWPPicker.setSelectedItemPosition(selectedIndex);
        }
        mViewBinding.includeWheelPicker.firstItemWheelPickerWPPicker.invalidate();

        // 第二个滚轮处理
        List<String> secondItems = new ArrayList<String>();
        selectedIndex = 0;
        for (int i = 0; i < 60; i++) {
            secondItems.add(String.format(Locale.getDefault(), "%02d", i));
            if (secondInitValue.equals(secondItems.get(i))) {
                selectedIndex = i;
            }
        }
        mViewBinding.includeWheelPicker.secondItemWheelPickerWPPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                mSecondValue = data.toString();
                mViewBinding.includeWheelPicker.twoItemWheelPickerLblValue.setText(mFirstValue + ":" + mSecondValue);
            }
        });
        // 加载两次数据是为了正确初始选中位置
        for (int i = 0; i < 2; i++) {
            mViewBinding.includeWheelPicker.secondItemWheelPickerWPPicker.setData(secondItems);
            mViewBinding.includeWheelPicker.secondItemWheelPickerWPPicker.setSelectedItemPosition(selectedIndex);
        }
        mViewBinding.includeWheelPicker.secondItemWheelPickerWPPicker.invalidate();

        mViewBinding.includeWheelPicker.twoItemWheelPickerRLPicker.setVisibility(View.VISIBLE);
    }

    // 周循环处理
    private void processWeekReport() {
        mViewBinding.setTimeImgEveryday.setVisibility(mConditionTime.isEveryDay() ? View.VISIBLE : View.GONE);
        mViewBinding.setTimeImgWorkday.setVisibility(mConditionTime.isWorkDay() ? View.VISIBLE : View.GONE);
        mViewBinding.setTimeImgWeekend.setVisibility(mConditionTime.isWeekEnd() ? View.VISIBLE : View.GONE);
        mViewBinding.setTimeImgSefldefine.setVisibility(mConditionTime.isSelfDefine() ? View.VISIBLE : View.GONE);
        mViewBinding.setTimeLblSefldefine.setVisibility(View.GONE);
        if (mConditionTime.isSelfDefine()) {
            mViewBinding.setTimeLblSefldefine.setVisibility(View.VISIBLE);
            //this.mLblSelfDefine.setText(String.format(": %s", this.mConditionTime.getWeekRepeatString(this)));
            mViewBinding.setTimeLblSefldefine.setText(String.format(Locale.getDefault(), " %s", mConditionTime.getWeekRepeatString(this)));
            mViewBinding.setTimeLblSefldefineHint.setText(R.string.set_time_selfdefine);
        } else {
            mViewBinding.setTimeLblSefldefineHint.setText(R.string.set_time_selfdefine_2);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivitySetTimeBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        // 创建时间条件实例
        Intent intent = getIntent();
        String cron = intent.getStringExtra("cron");
        mConditionTime = new EScene.conditionTimeEntry(cron);

        mViewBinding.includeToolbar.includeTitleLblTitle.setText(R.string.set_time_title);

        // 回退处理
        mViewBinding.includeToolbar.includeTitleImgBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 控件引用
        mViewBinding.includeWheelPicker.twoItemWheelPickerRLPicker.setVisibility(View.GONE);

        // 全天开关处理
        mViewBinding.setTimeSwtAllday.setChecked(mConditionTime.isAllDay());
        mViewBinding.setTimeSwtAllday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mViewBinding.setTimeLblBegin.setText("00:00");
                    mViewBinding.setTimeLblEnd.setText("23:59");
                    mConditionTime.beginHour = 0;
                    mConditionTime.beginMinute = 0;
                    mConditionTime.endHour = 23;
                    mConditionTime.endMinute = 59;
                }
            }
        });
        mViewBinding.setTimeLblBegin.setText(String.format(Locale.getDefault(), "%02d:%02d", mConditionTime.beginHour, mConditionTime.beginMinute));
        mViewBinding.setTimeLblEnd.setText(String.format(Locale.getDefault(), "%02d:%02d", mConditionTime.endHour, mConditionTime.endMinute));

        // 开始时间滚轮处理
        mViewBinding.setTimeImgBegin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWheelPicker(1, String.format(Locale.getDefault(), "%02d", mConditionTime.beginHour),
                        String.format(Locale.getDefault(), "%02d", mConditionTime.beginMinute));
            }
        });

        // 结束时间滚轮处理
        mViewBinding.setTimeImgEnd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWheelPicker(2, String.format(Locale.getDefault(), "%02d", mConditionTime.endHour),
                        String.format(Locale.getDefault(), "%02d", mConditionTime.endMinute));
            }
        });

        // 周循环处理
        processWeekReport();

        // 每一天处理
        mViewBinding.setTimeRllEveryday.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewBinding.includeWheelPicker.twoItemWheelPickerRLPicker.getVisibility() == View.VISIBLE) {
                    return;
                }
                mViewBinding.setTimeImgEveryday.setVisibility(View.VISIBLE);
                mViewBinding.setTimeImgWorkday.setVisibility(View.GONE);
                mViewBinding.setTimeImgWeekend.setVisibility(View.GONE);
                mViewBinding.setTimeImgSefldefine.setVisibility(View.GONE);
                mConditionTime.quickGenRepeat(1);
                mViewBinding.setTimeLblSefldefine.setVisibility(View.GONE);
                mViewBinding.setTimeLblSefldefineHint.setText(R.string.set_time_selfdefine_2);
            }
        });

        // 工作日处理
        mViewBinding.setTimeRllWorkday.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewBinding.includeWheelPicker.twoItemWheelPickerRLPicker.getVisibility() == View.VISIBLE) {
                    return;
                }
                mViewBinding.setTimeImgEveryday.setVisibility(View.GONE);
                mViewBinding.setTimeImgWorkday.setVisibility(View.VISIBLE);
                mViewBinding.setTimeImgWeekend.setVisibility(View.GONE);
                mViewBinding.setTimeImgSefldefine.setVisibility(View.GONE);
                mConditionTime.quickGenRepeat(2);
                mViewBinding.setTimeLblSefldefine.setVisibility(View.GONE);
                mViewBinding.setTimeLblSefldefineHint.setText(R.string.set_time_selfdefine_2);
            }
        });

        // 周末处理
        mViewBinding.setTimeRllWeekend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewBinding.includeWheelPicker.twoItemWheelPickerRLPicker.getVisibility() == View.VISIBLE) {
                    return;
                }
                mViewBinding.setTimeImgEveryday.setVisibility(View.GONE);
                mViewBinding.setTimeImgWorkday.setVisibility(View.GONE);
                mViewBinding.setTimeImgWeekend.setVisibility(View.VISIBLE);
                mViewBinding.setTimeImgSefldefine.setVisibility(View.GONE);
                mConditionTime.quickGenRepeat(3);
                mViewBinding.setTimeLblSefldefine.setVisibility(View.GONE);
                mViewBinding.setTimeLblSefldefineHint.setText(R.string.set_time_selfdefine_2);
            }
        });

        // 自定义处理
        mViewBinding.setTimeRllSefldefine.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewBinding.includeWheelPicker.twoItemWheelPickerRLPicker.getVisibility() == View.VISIBLE) {
                    return;
                }
                mViewBinding.setTimeImgEveryday.setVisibility(View.GONE);
                mViewBinding.setTimeImgWorkday.setVisibility(View.GONE);
                mViewBinding.setTimeImgWeekend.setVisibility(View.GONE);

                List<EChoice.itemEntry> items = mConditionTime.getReportChoiceItems(SetTimeActivity.this);
                Intent intent = new Intent(SetTimeActivity.this, ChoiceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.set_time_seldefine_select));
                bundle.putBoolean("isMultipleSelect", true);
                bundle.putInt("resultCode", Constant.RESULTCODE_CALLCHOICEACTIVITY_TIME);
                bundle.putSerializable("items", (Serializable) items);
                intent.putExtras(bundle);
                startActivityForResult(intent, Constant.REQUESTCODE_CALLCHOICEACTIVITY);
            }
        });

        // 确认处理
        mViewBinding.setTimeRelConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("cron", mConditionTime.genCronString());
                intent.putExtras(bundle);
                setResult(Constant.RESULTCODE_CALLSETTIMEACTIVITY, intent);
                finish();
            }
        });
        initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 刷新数据
        processWeekReport();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 处理自定义周循环
        if (requestCode == Constant.REQUESTCODE_CALLCHOICEACTIVITY && resultCode == Constant.RESULTCODE_CALLCHOICEACTIVITY_TIME) {
            Bundle bundle = data.getExtras();
            String values = bundle.getString("value");
            if (values != null && values.length() > 0) {
                String[] days = values.split(",");
                mConditionTime.repeat.clear();
                for (String day : days) {
                    if (day != null && day.length() > 0) {
                        mConditionTime.addWeekRepeat(Integer.parseInt(day));
                    }
                }
                // 刷新数据
                processWeekReport();
            }
        }
    }
}