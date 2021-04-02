package com.rexense.imoco.view;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.presenter.TSLHelper;
import com.vise.log.ViseLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AirConditionerForFSSActivity extends DetailActivity {
    @BindView(R.id.includeDetailImgMore)
    ImageView mTopbarMore;
    @BindView(R.id.includeDetailImgBack)
    ImageView mTopbarBack;
    @BindView(R.id.includeDetailLblTitle)
    TextView mTopbarTitle;
    @BindView(R.id.sub_tv)
    TextView mSubTV;
    @BindView(R.id.add_tv)
    TextView mAddTV;
    @BindView(R.id.workmode_ic)
    TextView mWorkModeIC;
    @BindView(R.id.workmode_tv)
    TextView mWorkModeTV;
    @BindView(R.id.switch_ic)
    TextView mSwitchIC;
    @BindView(R.id.switch_tv)
    TextView mSwitchTV;
    @BindView(R.id.fanmode_ic)
    TextView mFanModeIC;
    @BindView(R.id.fanmode_tv)
    TextView mFanModeTV;
    @BindView(R.id.target_tem_show_tv)
    TextView mTargetTemShowTV;
    @BindView(R.id.target_tem_tv)
    TextView mTargetTemTV;
    @BindView(R.id.current_tem_tv)
    TextView mCurrentTemTV;
    @BindView(R.id.switch_layout)
    RelativeLayout mSwitchLayout;
    @BindView(R.id.workmode_layout)
    RelativeLayout mWorkModeLayout;
    @BindView(R.id.fanmode_layout)
    RelativeLayout mFanModeLayout;

    private int mTargetTem = 20;
    private String[] mWorkModes;
    private String[] mWorkModeICs;
    private int mWorkMode = 1;
    private String[] mFanModes;
    private String[] mFanModeICs;
    private int mFanMode = 1;

    private TSLHelper mTSLHelper;

    // 更新状态
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (propertyEntry == null || propertyEntry.properties == null || propertyEntry.properties.size() == 0) {
            return false;
        }
        // 室温
        if (propertyEntry.getPropertyValue(CTSL.FSS_CurrentTemperature_1) != null && propertyEntry.getPropertyValue(CTSL.FSS_CurrentTemperature_1).length() > 0) {
            String currentTem = propertyEntry.getPropertyValue(CTSL.FSS_CurrentTemperature_1);
            mCurrentTemTV.setText(currentTem);
        }
        // 目标温度
        if (propertyEntry.getPropertyValue(CTSL.FSS_TargetTemperature_1) != null && propertyEntry.getPropertyValue(CTSL.FSS_TargetTemperature_1).length() > 0) {
            String targetTem = propertyEntry.getPropertyValue(CTSL.FSS_TargetTemperature_1);
            mTargetTem = Integer.parseInt(targetTem);
            mTargetTemShowTV.setText(targetTem);
            mTargetTemTV.setText(targetTem);
        }

        // 开关
        if (propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_1) != null && propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_1).length() > 0) {
            String powerSwitch = propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_1);
            int power = Integer.parseInt(powerSwitch);
            refreshViewState(power);
        }

        // 风速
        if (propertyEntry.getPropertyValue(CTSL.FSS_WindSpeed_1) != null && propertyEntry.getPropertyValue(CTSL.FSS_WindSpeed_1).length() > 0) {
            String windSpeed = propertyEntry.getPropertyValue(CTSL.FSS_WindSpeed_1);
            mFanMode = Integer.parseInt(windSpeed) - 2;
            mFanModeTV.setText(mFanModes[mFanMode]);
            mFanModeIC.setText(mFanModeICs[mFanMode]);
        }

        // 工作模式
        if (propertyEntry.getPropertyValue(CTSL.FSS_WorkMode_1) != null && propertyEntry.getPropertyValue(CTSL.FSS_WorkMode_1).length() > 0) {
            String workMode = propertyEntry.getPropertyValue(CTSL.FSS_WorkMode_1);
            mWorkMode = Integer.parseInt(workMode) - 1;
            mWorkModeTV.setText(mWorkModes[mWorkMode]);
            mWorkModeIC.setText(mWorkModeICs[mWorkMode]);
        }

        if (mSwitchIC.getCurrentTextColor() != getResources().getColor(R.color.all_8_2)) {
            if ((mWorkMode == 2 || mWorkMode == 3)) {
                mAddTV.setTextColor(getResources().getColor(R.color.white2));
                mSubTV.setTextColor(getResources().getColor(R.color.white2));
                if (mWorkMode == 3) {
                    mFanModeIC.setTextColor(getResources().getColor(R.color.all_8_2));
                    mFanModeTV.setTextColor(getResources().getColor(R.color.all_8_2));
                }
            } else {
                mAddTV.setTextColor(getResources().getColor(R.color.white));
                mSubTV.setTextColor(getResources().getColor(R.color.white));
                mFanModeIC.setTextColor(getResources().getColor(R.color.index_imgcolor));
                mFanModeTV.setTextColor(getResources().getColor(R.color.index_imgcolor));
            }
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_conditioner_for_full_screen);
        ButterKnife.bind(this);

        initStatusBar();

        mTSLHelper = new TSLHelper(this);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/jk/iconfont.ttf");
        mSubTV.setTypeface(iconfont);
        mAddTV.setTypeface(iconfont);
        mWorkModeIC.setTypeface(iconfont);
        mSwitchIC.setTypeface(iconfont);
        mFanModeIC.setTypeface(iconfont);

        mWorkModes = getResources().getStringArray(R.array.work_modes);
        mWorkModeICs = getResources().getStringArray(R.array.work_mode_ics);

        mFanModes = getResources().getStringArray(R.array.fan_modes);
        mFanModeICs = getResources().getStringArray(R.array.fan_mode_ics);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTopbarTitle.setText(R.string.air_conditioner);
        mTopbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(getColor(R.color.blue2));
        }

        mTopbarMore.setVisibility(View.GONE);
    }

    @OnClick({R.id.switch_layout, R.id.sub_tv, R.id.add_tv, R.id.workmode_layout, R.id.fanmode_layout})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.switch_layout: {
                // 开关
                if (mSwitchIC.getCurrentTextColor() == getResources().getColor(R.color.index_imgcolor)) {
                    // 关闭
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_OFF});
                } else {
                    // 打开
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_ON});
                }
                break;
            }
            case R.id.sub_tv: {
                // 减
                if (mSubTV.getCurrentTextColor() == getResources().getColor(R.color.white)) {
                    if (mTargetTem > 16) {
                        mTargetTem--;
                    }
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_TargetTemperature_1}, new String[]{"" + mTargetTem});
                }
                break;
            }
            case R.id.add_tv: {
                // 加
                if (mAddTV.getCurrentTextColor() == getResources().getColor(R.color.white)) {
                    if (mTargetTem < 32) {
                        mTargetTem++;
                    }
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_TargetTemperature_1}, new String[]{"" + mTargetTem});
                }
                break;
            }
            case R.id.workmode_layout: {
                // 模式
                if (mWorkModeIC.getCurrentTextColor() != getResources().getColor(R.color.all_8_2)) {
                    if (mWorkMode == 3) {
                        mWorkMode = 0;
                    } else {
                        mWorkMode++;
                    }
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_WorkMode_1}, new String[]{"" + (mWorkMode + 1)});
                }
                break;
            }
            case R.id.fanmode_layout: {
                // 风速
                if (mFanModeIC.getCurrentTextColor() != getResources().getColor(R.color.all_8_2)) {
                    if (mFanMode == 2) {
                        mFanMode = 0;
                    } else {
                        mFanMode++;
                    }
                    mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_WindSpeed_1}, new String[]{"" + (mFanMode + 2)});
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
                mSwitchIC.setTextColor(getResources().getColor(R.color.all_8_2));
                mSwitchTV.setTextColor(getResources().getColor(R.color.all_8_2));
                mWorkModeIC.setTextColor(getResources().getColor(R.color.all_8_2));
                mWorkModeTV.setTextColor(getResources().getColor(R.color.all_8_2));
                mFanModeIC.setTextColor(getResources().getColor(R.color.all_8_2));
                mFanModeTV.setTextColor(getResources().getColor(R.color.all_8_2));
                mSubTV.setTextColor(getResources().getColor(R.color.white2));
                mAddTV.setTextColor(getResources().getColor(R.color.white2));
                mTargetTemShowTV.setTextColor(getResources().getColor(R.color.white2));
                mTargetTemTV.setTextColor(getResources().getColor(R.color.white2));
                break;
            }
            case 1: {
                // 打开
                mSwitchIC.setTextColor(getResources().getColor(R.color.index_imgcolor));
                mSwitchTV.setTextColor(getResources().getColor(R.color.index_imgcolor));
                mWorkModeIC.setTextColor(getResources().getColor(R.color.index_imgcolor));
                mWorkModeTV.setTextColor(getResources().getColor(R.color.index_imgcolor));
                mFanModeIC.setTextColor(getResources().getColor(R.color.index_imgcolor));
                mFanModeTV.setTextColor(getResources().getColor(R.color.index_imgcolor));
                mSubTV.setTextColor(getResources().getColor(R.color.white));
                mAddTV.setTextColor(getResources().getColor(R.color.white));
                mTargetTemShowTV.setTextColor(getResources().getColor(R.color.white));
                mTargetTemTV.setTextColor(getResources().getColor(R.color.white));
                break;
            }
        }
    }
}