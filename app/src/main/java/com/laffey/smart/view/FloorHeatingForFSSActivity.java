package com.laffey.smart.view;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.presenter.PluginHelper;
import com.laffey.smart.presenter.TSLHelper;
import com.laffey.smart.view.AlignTextView;
import com.laffey.smart.view.DetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FloorHeatingForFSSActivity extends DetailActivity {
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
    @BindView(R.id.switch_layout)
    RelativeLayout mSwitchLayout;
    @BindView(R.id.timing_layout)
    RelativeLayout mTimingLayout;

    private int mPowerSwitch = 0;// 0: 关闭  1: 打开
    private int mTargetTem = 0;

    private TSLHelper mTSLHelper;

    // 更新状态
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (propertyEntry == null || propertyEntry.properties == null || propertyEntry.properties.size() == 0) {
            return false;
        }

        // 开关
        if (propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_3) != null && propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_3).length() > 0) {
            String powerSwitch = propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_3);
            mPowerSwitch = Integer.parseInt(powerSwitch);
            refreshViewState(mPowerSwitch);
        }

        // 目标温度
        if (propertyEntry.getPropertyValue(CTSL.FSS_TargetTemperature_3) != null && propertyEntry.getPropertyValue(CTSL.FSS_TargetTemperature_3).length() > 0) {
            String targetTem = propertyEntry.getPropertyValue(CTSL.FSS_TargetTemperature_3);
            mTargetTem = Integer.parseInt(targetTem);
            mTemValue.setText(String.valueOf(mTargetTem));
            mTemValue2TV.setText(String.valueOf(mTargetTem));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                mTemSeekBar.setProgress(mTargetTem - 16, true);
            else mTemSeekBar.setProgress(mTargetTem - 16);
        }

        // 当前温度
        if (propertyEntry.getPropertyValue(CTSL.FSS_CurrentTemperature_1) != null && propertyEntry.getPropertyValue(CTSL.FSS_CurrentTemperature_1).length() > 0) {
            String currentTem = propertyEntry.getPropertyValue(CTSL.FSS_CurrentTemperature_1);
            mTemValue1TV.setText(currentTem);
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
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/jk/iconfont.ttf");
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
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_TargetTemperature_3}, new String[]{"" + (16 + seekBar.getProgress())});
            }
        });
        mTemValue.setText("--");
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mTopbarMore.setVisibility(View.GONE);
    }

    @OnClick({R.id.switch_layout, R.id.timing_layout})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.switch_layout: {
                // 开关
                if (mPowerSwitch == 1) {
                    // 关闭
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_PowerSwitch_3}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    // 打开
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_PowerSwitch_3}, new String[]{"" + CTSL.STATUS_ON});
                }
                break;
            }
            case R.id.timing_layout: {
                // 定时
                if (mPowerSwitch == 1) {
                    PluginHelper.cloudTimer(FloorHeatingForFSSActivity.this, mIOTId, mProductKey);
                }
                break;
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
                mSwitchIC.setTextColor(getResources().getColor(R.color.orange3));
                mTemValue.setTextColor(getResources().getColor(R.color.orange3));
                mTemValue1TV.setTextColor(getResources().getColor(R.color.orange3));
                mTemValue2TV.setTextColor(getResources().getColor(R.color.orange3));
                mTemUnit1TV.setTextColor(getResources().getColor(R.color.orange3));
                mTemUnit2TV.setTextColor(getResources().getColor(R.color.orange3));
                mTimingIC.setTextColor(getResources().getColor(R.color.orange3));
                mTemSeekBar.setEnabled(false);
                break;
            }
            case 1: {
                // 打开
                mSwitchIC.setTextColor(getResources().getColor(R.color.orange2));
                mTemValue.setTextColor(getResources().getColor(R.color.orange2));
                mTemValue1TV.setTextColor(getResources().getColor(R.color.orange2));
                mTemValue2TV.setTextColor(getResources().getColor(R.color.orange2));
                mTemUnit1TV.setTextColor(getResources().getColor(R.color.orange2));
                mTemUnit2TV.setTextColor(getResources().getColor(R.color.orange2));
                mTimingIC.setTextColor(getResources().getColor(R.color.orange2));
                mTemSeekBar.setEnabled(true);
                break;
            }
        }
    }
}