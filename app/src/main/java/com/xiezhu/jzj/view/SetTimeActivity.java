package com.xiezhu.jzj.view;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.vise.log.ViseLog;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.model.EChoice;
import com.xiezhu.jzj.model.EScene;

/**
 * Creator: xieshaobing
 * creat time: 2020-06-11 17:14
 * Description: 设置时间界面
 */
public class SetTimeActivity extends Activity {
    private EScene.conditionTimeEntry mConditionTime;
    private Switch mSthAllday;
    private TextView mLblBegin, mLblEnd, mLblSelfDefine;
    private ImageView mImgBegin, mImgEnd, mImgEveryday, mImgWorkday, mImgWeekend, mImgSelfdefine;
    private RelativeLayout mWheelPickerLayout;
    private WheelPicker mFirstWheelPicker, mSecondWheelPicker;
    private TextView mWheelPickerValue;
    private String mFirstValue, mSecondValue;
    private int mType;

    // 设置滑轮选择器
    @SuppressLint("SetTextI18n")
    private void setWheelPicker(int type, String firstInitValue, String secondInitValue) {
        mType = type;
        mWheelPickerValue.setText(firstInitValue + " : " + secondInitValue);
        mFirstValue = firstInitValue;
        mSecondValue = secondInitValue;
        TextView ok = findViewById(R.id.twoItemWheelPickerLblOk);
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWheelPickerLayout.setVisibility(View.GONE);
                if (mType == 1) {
                    mLblBegin.setText(mFirstValue + ":" + mSecondValue);
                    mConditionTime.beginHour = Integer.parseInt(mFirstValue);
                    mConditionTime.beginMinute = Integer.parseInt(mSecondValue);
                } else {
                    mLblEnd.setText(mFirstValue + ":" + mSecondValue);
                    mConditionTime.endHour = Integer.parseInt(mFirstValue);
                    mConditionTime.endMinute = Integer.parseInt(mSecondValue);
                }
                mSthAllday.setChecked(mConditionTime.isAllDay());
            }
        });
        TextView cancel = findViewById(R.id.twoItemWheelPickerLblCancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWheelPickerLayout.setVisibility(View.GONE);
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
        mFirstWheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                mFirstValue = data.toString();
                mWheelPickerValue.setText(mFirstValue + ":" + mSecondValue);
            }
        });
        // 加载两次数据是为了正确初始选中位置
        for (int i = 0; i < 2; i++) {
            mFirstWheelPicker.setData(firstItems);
            mFirstWheelPicker.setSelectedItemPosition(selectedIndex);
        }
        mFirstWheelPicker.invalidate();

        // 第二个滚轮处理
        List<String> secondItems = new ArrayList<String>();
        selectedIndex = 0;
        for (int i = 0; i < 60; i++) {
            secondItems.add(String.format(Locale.getDefault(), "%02d", i));
            if (secondInitValue.equals(secondItems.get(i))) {
                selectedIndex = i;
            }
        }
        mSecondWheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                mSecondValue = data.toString();
                mWheelPickerValue.setText(mFirstValue + ":" + mSecondValue);
            }
        });
        // 加载两次数据是为了正确初始选中位置
        for (int i = 0; i < 2; i++) {
            mSecondWheelPicker.setData(secondItems);
            mSecondWheelPicker.setSelectedItemPosition(selectedIndex);
        }
        mSecondWheelPicker.invalidate();

        mWheelPickerLayout.setVisibility(View.VISIBLE);
    }

    // 周循环处理
    private void processWeekReport() {
        mImgEveryday.setVisibility(mConditionTime.isEveryDay() ? View.VISIBLE : View.GONE);
        mImgWorkday.setVisibility(mConditionTime.isWorkDay() ? View.VISIBLE : View.GONE);
        mImgWeekend.setVisibility(mConditionTime.isWeekEnd() ? View.VISIBLE : View.GONE);
        mImgSelfdefine.setVisibility(mConditionTime.isSelfDefine() ? View.VISIBLE : View.GONE);
        mLblSelfDefine.setVisibility(View.GONE);
        if (mConditionTime.isSelfDefine()) {
            mLblSelfDefine.setVisibility(View.VISIBLE);
            mLblSelfDefine.setText(String.format(": %s", mConditionTime.getWeekRepeatString(this)));
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time);

        // 创建时间条件实例
        Intent intent = getIntent();
        String cron = intent.getStringExtra("cron");
        mConditionTime = new EScene.conditionTimeEntry(cron);

        TextView title = findViewById(R.id.includeTitleLblTitle);
        title.setText(R.string.set_time_title);

        // 回退处理
        ImageView imgBack = findViewById(R.id.includeTitleImgBack);
        imgBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 控件引用
        mLblBegin = findViewById(R.id.setTimeLblBegin);
        mLblEnd = findViewById(R.id.setTimeLblEnd);
        mLblSelfDefine = findViewById(R.id.setTimeLblSefldefine);
        mImgBegin = findViewById(R.id.setTimeImgBegin);
        mImgEnd = findViewById(R.id.setTimeImgEnd);
        mImgEveryday = findViewById(R.id.setTimeImgEveryday);
        mImgWorkday = findViewById(R.id.setTimeImgWorkday);
        mImgWeekend = findViewById(R.id.setTimeImgWeekend);
        mImgSelfdefine = findViewById(R.id.setTimeImgSefldefine);
        mWheelPickerLayout = findViewById(R.id.twoItemWheelPickerRLPicker);
        mWheelPickerLayout.setVisibility(View.GONE);
        mWheelPickerValue = findViewById(R.id.twoItemWheelPickerLblValue);
        mFirstWheelPicker = findViewById(R.id.firstItemWheelPickerWPPicker);
        mSecondWheelPicker = findViewById(R.id.secondItemWheelPickerWPPicker);

        // 全天开关处理
        mSthAllday = findViewById(R.id.setTimeSwtAllday);
        mSthAllday.setChecked(mConditionTime.isAllDay());
        mSthAllday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLblBegin.setText("00:00");
                    mLblEnd.setText("23:59");
                    mConditionTime.beginHour = 0;
                    mConditionTime.beginMinute = 0;
                    mConditionTime.endHour = 23;
                    mConditionTime.endMinute = 59;
                }
            }
        });
        mLblBegin.setText(String.format(Locale.getDefault(), "%02d:%02d", mConditionTime.beginHour, mConditionTime.beginMinute));
        mLblEnd.setText(String.format(Locale.getDefault(), "%02d:%02d", mConditionTime.endHour, mConditionTime.endMinute));

        // 开始时间滚轮处理
        mImgBegin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWheelPicker(1, String.format(Locale.getDefault(), "%02d", mConditionTime.beginHour),
                        String.format(Locale.getDefault(), "%02d", mConditionTime.beginMinute));
            }
        });

        // 结束时间滚轮处理
        mImgEnd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWheelPicker(2, String.format(Locale.getDefault(), "%02d", mConditionTime.endHour),
                        String.format(Locale.getDefault(), "%02d", mConditionTime.endMinute));
            }
        });

        // 周循环处理
        processWeekReport();

        // 每一天处理
        RelativeLayout rllEveryday = findViewById(R.id.setTimeRllEveryday);
        rllEveryday.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWheelPickerLayout.getVisibility() == View.VISIBLE) {
                    return;
                }
                mImgEveryday.setVisibility(View.VISIBLE);
                mImgWorkday.setVisibility(View.GONE);
                mImgWeekend.setVisibility(View.GONE);
                mImgSelfdefine.setVisibility(View.GONE);
                mConditionTime.quickGenRepeat(1);
                mLblSelfDefine.setVisibility(View.GONE);
            }
        });

        // 工作日处理
        RelativeLayout rllWorkday = findViewById(R.id.setTimeRllWorkday);
        rllWorkday.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWheelPickerLayout.getVisibility() == View.VISIBLE) {
                    return;
                }
                mImgEveryday.setVisibility(View.GONE);
                mImgWorkday.setVisibility(View.VISIBLE);
                mImgWeekend.setVisibility(View.GONE);
                mImgSelfdefine.setVisibility(View.GONE);
                mConditionTime.quickGenRepeat(2);
                mLblSelfDefine.setVisibility(View.GONE);
            }
        });

        // 周末处理
        RelativeLayout rllWeekend = findViewById(R.id.setTimeRllWeekend);
        rllWeekend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWheelPickerLayout.getVisibility() == View.VISIBLE) {
                    return;
                }
                mImgEveryday.setVisibility(View.GONE);
                mImgWorkday.setVisibility(View.GONE);
                mImgWeekend.setVisibility(View.VISIBLE);
                mImgSelfdefine.setVisibility(View.GONE);
                mConditionTime.quickGenRepeat(3);
                mLblSelfDefine.setVisibility(View.GONE);
            }
        });

        // 自定义处理
        RelativeLayout rllSelfdefine = findViewById(R.id.setTimeRllSefldefine);
        rllSelfdefine.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWheelPickerLayout.getVisibility() == View.VISIBLE) {
                    return;
                }
                mImgEveryday.setVisibility(View.GONE);
                mImgWorkday.setVisibility(View.GONE);
                mImgWeekend.setVisibility(View.GONE);
                mImgSelfdefine.setVisibility(View.VISIBLE);
                mLblSelfDefine.setVisibility(View.VISIBLE);
                mLblSelfDefine.setText(String.format(Locale.getDefault(), ": %s", mConditionTime.getWeekRepeatString(SetTimeActivity.this)));

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
        RelativeLayout relConfirm = findViewById(R.id.setTimeRelConfirm);
        relConfirm.setOnClickListener(new OnClickListener() {
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

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }
}