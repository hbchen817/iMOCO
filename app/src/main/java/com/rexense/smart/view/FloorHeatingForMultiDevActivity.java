package com.rexense.smart.view;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.rexense.smart.R;
import com.rexense.smart.contract.CTSL;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.model.ETSL;
import com.rexense.smart.presenter.PluginHelper;
import com.rexense.smart.presenter.TSLHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FloorHeatingForMultiDevActivity extends DetailActivity {
    @BindView(R.id.topbar)
    RelativeLayout mTopbarRoot;
    @BindView(R.id.includeDetailImgMore)
    ImageView mTopbarMore;
    @BindView(R.id.includeDetailImgBack)
    ImageView mTopbarBack;
    @BindView(R.id.includeDetailLblTitle)
    TextView mTopbarTitle;
    @BindView(R.id.icon_switch)
    TextView mSwitchIC;
    @BindView(R.id.timing_ic)
    TextView mTimingIC;
    @BindView(R.id.temperature_value)
    AlignTextView mTemValue;
    @BindView(R.id.temperatureSeekBar)
    SeekBar mTemSeekBar;
    @BindView(R.id.temperature)
    TextView mTemValue1TV;
    @BindView(R.id.tem_unit_1)
    TextView mTemUnit1TV;
    @BindView(R.id.tem_unit_2)
    TextView mTemUnit2TV;
    @BindView(R.id.temperature2)
    TextView mTemValue2TV;
    @BindView(R.id.auto_work_mode_tv)
    TextView mAutoWorkModeTV;
    @BindView(R.id.switch_layout)
    RelativeLayout mSwitchLayout;
    @BindView(R.id.timing_layout)
    RelativeLayout mTimingLayout;

    private int mPowerSwitch = 0;// 0: 关闭  1: 打开
    private int mTargetTem = 0;

    private TSLHelper mTSLHelper;

    private int mOrange3;
    private int mOrange2;

    // 更新状态
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (propertyEntry == null || propertyEntry.properties == null || propertyEntry.properties.size() == 0) {
            return false;
        }

        // 开关
        if (propertyEntry.getPropertyValue(CTSL.M3I1_PowerSwitch_FloorHeating) != null && propertyEntry.getPropertyValue(CTSL.M3I1_PowerSwitch_FloorHeating).length() > 0) {
            String powerSwitch = propertyEntry.getPropertyValue(CTSL.M3I1_PowerSwitch_FloorHeating);
            mPowerSwitch = Integer.parseInt(powerSwitch);
            refreshViewState(mPowerSwitch);
        }

        // 目标温度
        if (propertyEntry.getPropertyValue(CTSL.M3I1_TargetTemperature_FloorHeating) != null
                && propertyEntry.getPropertyValue(CTSL.M3I1_TargetTemperature_FloorHeating).length() > 0) {
            String targetTem = propertyEntry.getPropertyValue(CTSL.M3I1_TargetTemperature_FloorHeating);
            mTargetTem = Integer.parseInt(targetTem);
            mTemValue.setText(String.valueOf(mTargetTem));
            mTemValue2TV.setText(String.valueOf(mTargetTem));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                mTemSeekBar.setProgress(mTargetTem - 16, true);
            else mTemSeekBar.setProgress(mTargetTem - 16);
        }

        // 当前温度
        if (propertyEntry.getPropertyValue(CTSL.M3I1_CurrentTemperature_FloorHeating) != null
                && propertyEntry.getPropertyValue(CTSL.M3I1_CurrentTemperature_FloorHeating).length() > 0) {
            String currentTem = propertyEntry.getPropertyValue(CTSL.M3I1_CurrentTemperature_FloorHeating);
            mTemValue1TV.setText(currentTem);
        }

        // 加热状态
        if (propertyEntry.getPropertyValue(CTSL.M3I1_AutoWorkMode_FloorHeating) != null
                && propertyEntry.getPropertyValue(CTSL.M3I1_AutoWorkMode_FloorHeating).length() > 0) {
            String autoWorkMode = propertyEntry.getPropertyValue(CTSL.M3I1_AutoWorkMode_FloorHeating);
            mAutoWorkModeTV.setText("0".equals(autoWorkMode) ? R.string.close : R.string.open);
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_heating_for_full_screen);
        ButterKnife.bind(this);

        mTSLHelper = new TSLHelper(this);

        initStatusBar();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTopbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTopbarTitle.setText(R.string.floor_heating);
    }

    private void initView() {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mSwitchIC.setTypeface(iconfont);
        mTimingIC.setTypeface(iconfont);

        mTemSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String tem = String.valueOf(16 + progress);
                mTemValue.setText(tem);
                mTemValue2TV.setText(tem);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.M3I1_TargetTemperature_FloorHeating},
                        new String[]{"" + (16 + seekBar.getProgress())});
            }
        });
        mTemValue.setText("--");

        mOrange3 = ContextCompat.getColor(this, R.color.orange3);
        mOrange2 = ContextCompat.getColor(this, R.color.orange2);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mTopbarRoot.setBackgroundColor(Color.WHITE);
        mTopbarMore.setVisibility(View.GONE);
    }

    @OnClick({R.id.switch_layout, R.id.timing_layout})
    protected void onViewClicked(View view) {
        int viewId = view.getId();
        if (viewId == R.id.switch_layout) {
            // 开关
            if (mPowerSwitch == 1) {
                // 关闭
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.M3I1_PowerSwitch_FloorHeating}, new String[]{"" + CTSL.STATUS_OFF});
            } else {
                // 打开
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.M3I1_PowerSwitch_FloorHeating}, new String[]{"" + CTSL.STATUS_ON});
            }
        } else if (viewId == R.id.timing_layout) {
            // 定时
            if (mPowerSwitch == 1) {
                PluginHelper.cloudTimer(this, mIOTId, mProductKey);
            }
        }
    }

    /**
     * 根据开关状态刷新ui
     *
     * @param flag 0:关闭 1:打开
     */
    private void refreshViewState(int flag) {
        switch (flag) {
            case 0: {
                // 关闭
                mSwitchIC.setTextColor(mOrange3);
                mTemValue.setTextColor(mOrange3);
                mTemValue1TV.setTextColor(mOrange3);
                mTemValue2TV.setTextColor(mOrange3);
                mAutoWorkModeTV.setTextColor(mOrange3);
                mTemUnit1TV.setTextColor(mOrange3);
                mTemUnit2TV.setTextColor(mOrange3);
                mTimingIC.setTextColor(mOrange3);
                mTemSeekBar.setEnabled(false);
                break;
            }
            case 1: {
                // 打开
                mSwitchIC.setTextColor(mOrange2);
                mTemValue.setTextColor(mOrange2);
                mTemValue1TV.setTextColor(mOrange2);
                mTemValue2TV.setTextColor(mOrange2);
                mAutoWorkModeTV.setTextColor(mOrange2);
                mTemUnit1TV.setTextColor(mOrange2);
                mTemUnit2TV.setTextColor(mOrange2);
                mTimingIC.setTextColor(mOrange2);
                mTemSeekBar.setEnabled(true);
                break;
            }
        }
    }
}