package com.rexense.imoco.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EChoice;
import com.rexense.imoco.model.EScene;

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
    private void setWheelPicker(int type, String firstInitValue, String secondInitValue) {
        this.mType = type;
        this.mWheelPickerValue.setText(firstInitValue + " : " + secondInitValue);
        mFirstValue = firstInitValue;
        mSecondValue = secondInitValue;
        TextView ok = (TextView)findViewById(R.id.twoItemWheelPickerLblOk);
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWheelPickerLayout.setVisibility(View.GONE);
                if(mType == 1){
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
        TextView cancel = (TextView)findViewById(R.id.twoItemWheelPickerLblCancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWheelPickerLayout.setVisibility(View.GONE);
            }
        });

        // 第一个滚轮处理
        List<String> firstItems = new ArrayList<String>();
        int selectedIndex = 0;
        for(int i = 0; i < 24; i++){
            firstItems.add(String.format("%02d", i));
            if(firstInitValue.equals(firstItems.get(i))){
                selectedIndex = i;
            }
        }
        this.mFirstWheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                mFirstValue = data.toString();
                mWheelPickerValue.setText(mFirstValue + ":" + mSecondValue);
            }
        });
        // 加载两次数据是为了正确初始选中位置
        for(int i = 0; i < 2; i++) {
            this.mFirstWheelPicker.setData(firstItems);
            this.mFirstWheelPicker.setSelectedItemPosition(selectedIndex);
        }
        this.mFirstWheelPicker.invalidate();

        // 第二个滚轮处理
        List<String> secondItems = new ArrayList<String>();
        selectedIndex = 0;
        for(int i = 0; i < 60; i++){
            secondItems.add(String.format("%02d", i));
            if(secondInitValue.equals(secondItems.get(i))){
                selectedIndex = i;
            }
        }
        this.mSecondWheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
            @Override
            public void onItemSelected(WheelPicker picker, Object data, int position) {
                mSecondValue = data.toString();
                mWheelPickerValue.setText(mFirstValue + ":" + mSecondValue);
            }
        });
        // 加载两次数据是为了正确初始选中位置
        for(int i = 0; i < 2; i++) {
            this.mSecondWheelPicker.setData(secondItems);
            this.mSecondWheelPicker.setSelectedItemPosition(selectedIndex);
        }
        this.mSecondWheelPicker.invalidate();

        this.mWheelPickerLayout.setVisibility(View.VISIBLE);
    }

    // 周循环处理
    private void processWeekReport(){
        this.mImgEveryday.setVisibility(this.mConditionTime.isEveryDay() ? View.VISIBLE : View.GONE);
        this.mImgWorkday.setVisibility(this.mConditionTime.isWorkDay() ? View.VISIBLE : View.GONE);
        this.mImgWeekend.setVisibility(this.mConditionTime.isWeekEnd() ? View.VISIBLE : View.GONE);
        this.mImgSelfdefine.setVisibility(this.mConditionTime.isSelfDefine() ? View.VISIBLE : View.GONE);
        this.mLblSelfDefine.setVisibility(View.GONE);
        if(this.mConditionTime.isSelfDefine()){
            this.mLblSelfDefine.setVisibility(View.VISIBLE);
            this.mLblSelfDefine.setText(String.format(": %s", this.mConditionTime.getWeekRepeatString(this)));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_time);

        // 创建时间条件实例
        Intent intent = getIntent();
        String cron = intent.getStringExtra("cron");
        this.mConditionTime = new EScene.conditionTimeEntry(cron);

        TextView title = (TextView)findViewById(R.id.includeTitleLblTitle);
        title.setText(R.string.set_time_title);

        // 回退处理
        ImageView imgBack = (ImageView)findViewById(R.id.includeTitleImgBack);
        imgBack.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 控件引用
        this.mLblBegin = (TextView) findViewById(R.id.setTimeLblBegin);
        this.mLblEnd = (TextView) findViewById(R.id.setTimeLblEnd);
        this.mLblSelfDefine = (TextView)findViewById(R.id.setTimeLblSefldefine);
        this.mImgBegin = (ImageView) findViewById(R.id.setTimeImgBegin);
        this.mImgEnd = (ImageView) findViewById(R.id.setTimeImgEnd);
        this.mImgEveryday = (ImageView) findViewById(R.id.setTimeImgEveryday);
        this.mImgWorkday = (ImageView) findViewById(R.id.setTimeImgWorkday);
        this.mImgWeekend = (ImageView) findViewById(R.id.setTimeImgWeekend);
        this.mImgSelfdefine = (ImageView) findViewById(R.id.setTimeImgSefldefine);
        this.mWheelPickerLayout = (RelativeLayout)findViewById(R.id.twoItemWheelPickerRLPicker);
        this.mWheelPickerLayout.setVisibility(View.GONE);
        this.mWheelPickerValue = (TextView)findViewById(R.id.twoItemWheelPickerLblValue);
        this.mFirstWheelPicker = (WheelPicker) findViewById(R.id.firstItemWheelPickerWPPicker);
        this.mSecondWheelPicker = (WheelPicker) findViewById(R.id.secondItemWheelPickerWPPicker);

        // 全天开关处理
        this.mSthAllday = (Switch)findViewById(R.id.setTimeSwtAllday);
        if(this.mConditionTime.isAllDay()){
            this.mSthAllday.setChecked(true);
        } else {
            this.mSthAllday.setChecked(false);
        }
        this.mSthAllday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mLblBegin.setText("00:00");
                    mLblEnd.setText("23:59");
                    mConditionTime.beginHour = 0;
                    mConditionTime.beginMinute = 0;
                    mConditionTime.endHour = 23;
                    mConditionTime.endMinute = 59;
                }
            }
        });
        this.mLblBegin.setText(String.format("%02d:%02d", this.mConditionTime.beginHour, this.mConditionTime.beginMinute));
        this.mLblEnd.setText(String.format("%02d:%02d", this.mConditionTime.endHour, this.mConditionTime.endMinute));

        // 开始时间滚轮处理
        this.mImgBegin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWheelPicker(1, String.format("%02d", mConditionTime.beginHour), String.format("%02d", mConditionTime.beginMinute));
            }
        });

        // 结束时间滚轮处理
        this.mImgEnd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWheelPicker(2, String.format("%02d", mConditionTime.endHour), String.format("%02d", mConditionTime.endMinute));
            }
        });

        // 周循环处理
        this.processWeekReport();

        // 每一天处理
        RelativeLayout rllEveryday = (RelativeLayout)findViewById(R.id.setTimeRllEveryday);
        rllEveryday.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWheelPickerLayout.getVisibility() == View.VISIBLE){
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
        RelativeLayout rllWorkday = (RelativeLayout)findViewById(R.id.setTimeRllWorkday);
        rllWorkday.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWheelPickerLayout.getVisibility() == View.VISIBLE){
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
        RelativeLayout rllWeekend = (RelativeLayout)findViewById(R.id.setTimeRllWeekend);
        rllWeekend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWheelPickerLayout.getVisibility() == View.VISIBLE){
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
        RelativeLayout rllSelfdefine = (RelativeLayout)findViewById(R.id.setTimeRllSefldefine);
        rllSelfdefine.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWheelPickerLayout.getVisibility() == View.VISIBLE){
                    return;
                }
                mImgEveryday.setVisibility(View.GONE);
                mImgWorkday.setVisibility(View.GONE);
                mImgWeekend.setVisibility(View.GONE);
                mImgSelfdefine.setVisibility(View.VISIBLE);
                mLblSelfDefine.setVisibility(View.VISIBLE);
                mLblSelfDefine.setText(String.format(": %s", mConditionTime.getWeekRepeatString(SetTimeActivity.this)));

                List<EChoice.itemEntry> items = mConditionTime.getReportChoiceItems(SetTimeActivity.this);
                Intent intent = new Intent(SetTimeActivity.this, ChoiceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", getString(R.string.set_time_seldefine_select));
                bundle.putBoolean("isMultipleSelect", true);
                bundle.putInt("resultCode", Constant.RESULTCODE_CALLCHOICEACTIVITY_TIME);
                bundle.putSerializable("items", (Serializable)items);
                intent.putExtras(bundle);
                startActivityForResult(intent, Constant.REQUESTCODE_CALLCHOICEACTIVITY);
            }
        });

        // 确认处理
        RelativeLayout relConfirm = (RelativeLayout)findViewById(R.id.setTimeRelConfirm);
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
    }

    @Override
    protected void onResume(){
        super.onResume();
        // 刷新数据
        processWeekReport();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 处理自定义周循环
        if(requestCode == Constant.REQUESTCODE_CALLCHOICEACTIVITY && resultCode == Constant.RESULTCODE_CALLCHOICEACTIVITY_TIME){
            Bundle bundle = data.getExtras();
            String values = bundle.getString("value");
            if(values != null && values.length() > 0){
                String[] days = values.split(",");
                mConditionTime.repeat.clear();
                for(String day : days){
                    if(day != null && day.length() > 0){
                        mConditionTime.addWeekRepeat(Integer.parseInt(day));
                    }
                }
                // 刷新数据
                processWeekReport();
            }
        }
    }
}