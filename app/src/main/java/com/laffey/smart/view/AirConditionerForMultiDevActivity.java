package com.laffey.smart.view;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityAirConditionerForFullScreenBinding;
import com.laffey.smart.model.ETSL;
import com.laffey.smart.presenter.PluginHelper;
import com.laffey.smart.presenter.TSLHelper;
import com.vise.log.ViseLog;

public class AirConditionerForMultiDevActivity extends DetailActivity {
    private ActivityAirConditionerForFullScreenBinding mViewBinding;

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
            mViewBinding.currentTemTv.setText(currentTem);
        }
        // 目标温度
        if (propertyEntry.getPropertyValue(CTSL.FSS_TargetTemperature_1) != null && propertyEntry.getPropertyValue(CTSL.FSS_TargetTemperature_1).length() > 0) {
            String targetTem = propertyEntry.getPropertyValue(CTSL.FSS_TargetTemperature_1);
            mTargetTem = Integer.parseInt(targetTem);
            mViewBinding.targetTemShowTv.setText(targetTem);
            mViewBinding.targetTemTv.setText(targetTem);
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
            mFanMode = Integer.parseInt(windSpeed);
            switch (mFanMode) {
                case 5: {
                    // 自动
                    mViewBinding.fanmodeTv.setText(mFanModes[0]);
                    mViewBinding.fanmodeIc.setText(mFanModeICs[0]);
                    break;
                }
                case 1: {
                    // 低风
                    mViewBinding.fanmodeTv.setText(mFanModes[1]);
                    mViewBinding.fanmodeIc.setText(mFanModeICs[1]);
                    break;
                }
                case 2: {
                    // 中风
                    mViewBinding.fanmodeTv.setText(mFanModes[2]);
                    mViewBinding.fanmodeIc.setText(mFanModeICs[2]);
                    break;
                }
                case 3: {
                    // 高风
                    mViewBinding.fanmodeTv.setText(mFanModes[3]);
                    mViewBinding.fanmodeIc.setText(mFanModeICs[3]);
                    break;
                }
            }

        }

        // 工作模式
        if (propertyEntry.getPropertyValue(CTSL.FSS_WorkMode_1) != null && propertyEntry.getPropertyValue(CTSL.FSS_WorkMode_1).length() > 0) {
            String workMode = propertyEntry.getPropertyValue(CTSL.FSS_WorkMode_1);
            mWorkMode = Integer.parseInt(workMode);
            switch (mWorkMode) {
                case 3: {
                    // 制冷
                    mViewBinding.workmodeTv.setText(mWorkModes[0]);
                    mViewBinding.workmodeIc.setText(mWorkModeICs[0]);
                    break;
                }
                case 4: {
                    // 制热
                    mViewBinding.workmodeTv.setText(mWorkModes[1]);
                    mViewBinding.workmodeIc.setText(mWorkModeICs[1]);
                    break;
                }
                case 7: {
                    // 送风
                    mViewBinding.workmodeTv.setText(mWorkModes[2]);
                    mViewBinding.workmodeIc.setText(mWorkModeICs[2]);
                    break;
                }
            }
        }

        if (mViewBinding.switchIc.getCurrentTextColor() != getResources().getColor(R.color.all_8_2)) {
            /*if ((mWorkMode == 2 || mWorkMode == 3)) {
                mViewBinding.addTv.setTextColor(getResources().getColor(R.color.white2));
                mViewBinding.subTv.setTextColor(getResources().getColor(R.color.white2));
                if (mWorkMode == 3) {
                    mViewBinding.fanmodeIc.setTextColor(getResources().getColor(R.color.all_8_2));
                    mViewBinding.fanmodeTv.setTextColor(getResources().getColor(R.color.all_8_2));
                }
            } else {
                mViewBinding.addTv.setTextColor(getResources().getColor(R.color.white));
                mViewBinding.subTv.setTextColor(getResources().getColor(R.color.white));
                mViewBinding.fanmodeIc.setTextColor(getResources().getColor(R.color.index_imgcolor));
                mViewBinding.fanmodeTv.setTextColor(getResources().getColor(R.color.index_imgcolor));
            }*/
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityAirConditionerForFullScreenBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();

        mTSLHelper = new TSLHelper(this);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.subTv.setTypeface(iconfont);
        mViewBinding.addTv.setTypeface(iconfont);
        mViewBinding.workmodeIc.setTypeface(iconfont);
        mViewBinding.switchIc.setTypeface(iconfont);
        mViewBinding.fanmodeIc.setTypeface(iconfont);
        mViewBinding.timingIc.setTypeface(iconfont);

        mWorkModes = getResources().getStringArray(R.array.work_modes_2);
        mWorkModeICs = getResources().getStringArray(R.array.work_mode_ics_2);

        mFanModes = getResources().getStringArray(R.array.fan_modes_2);
        mFanModeICs = getResources().getStringArray(R.array.fan_mode_ics_2);

        mViewBinding.switchLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.subTv.setOnClickListener(this::onViewClicked);
        mViewBinding.addTv.setOnClickListener(this::onViewClicked);
        mViewBinding.workmodeLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.fanmodeLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.timingLayout.setOnClickListener(this::onViewClicked);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewBinding.includeToolbar.includeDetailLblTitle.setText(R.string.air_conditioner);
        mViewBinding.includeToolbar.includeDetailImgBack.setOnClickListener(new View.OnClickListener() {
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
            getWindow().setStatusBarColor(getColor(R.color.appcolor));
        }

        mViewBinding.includeToolbar.includeDetailImgMore.setVisibility(View.GONE);
    }

    protected void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.switch_layout) {
            // 开关
            if (mViewBinding.switchIc.getCurrentTextColor() == getResources().getColor(R.color.index_imgcolor)) {
                // 关闭
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_OFF});
            } else {
                // 打开
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_PowerSwitch_1}, new String[]{"" + CTSL.STATUS_ON});
            }
        } else if (id == R.id.sub_tv) {
            // 减
            if (mViewBinding.subTv.getCurrentTextColor() == getResources().getColor(R.color.white)) {
                if (mTargetTem > 16) {
                    mTargetTem--;
                }
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_TargetTemperature_1}, new String[]{"" + mTargetTem});
            }
        } else if (id == R.id.add_tv) {
            // 加
            if (mViewBinding.addTv.getCurrentTextColor() == getResources().getColor(R.color.white)) {
                if (mTargetTem < 32) {
                    mTargetTem++;
                }
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_TargetTemperature_1}, new String[]{"" + mTargetTem});
            }
        } else if (id == R.id.workmode_layout) {
            // 模式
            if (mViewBinding.workmodeIc.getCurrentTextColor() != getResources().getColor(R.color.all_8_2)) {
                if (mWorkMode == 3) {
                    mWorkMode = 4;
                } else if (mWorkMode == 4) {
                    mWorkMode = 7;
                } else if (mWorkMode == 7) {
                    mWorkMode = 3;
                }
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_WorkMode_1}, new String[]{"" + mWorkMode});
            }
        } else if (id == R.id.fanmode_layout) {
            // 风速
            if (mViewBinding.fanmodeIc.getCurrentTextColor() != getResources().getColor(R.color.all_8_2)) {
                switch (mFanMode) {
                    case 5: {
                        // 自动
                        mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_WindSpeed_1}, new String[]{"1"});
                        break;
                    }
                    case 1: {
                        // 低风
                        mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_WindSpeed_1}, new String[]{"2"});
                        break;
                    }
                    case 2: {
                        // 中风
                        mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_WindSpeed_1}, new String[]{"3"});
                        break;
                    }
                    case 3: {
                        // 高风
                        mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_WindSpeed_1}, new String[]{"5"});
                        break;
                    }
                }
            }
        } else if (id == R.id.timing_layout) {
            // 定时
            if (mViewBinding.switchIc.getCurrentTextColor() == getResources().getColor(R.color.index_imgcolor)) {
                PluginHelper.cloudTimer(AirConditionerForMultiDevActivity.this, mIOTId, mProductKey);
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
                int all82 = getResources().getColor(R.color.all_8_2);
                mViewBinding.switchIc.setTextColor(all82);
                mViewBinding.switchTv.setTextColor(all82);
                mViewBinding.workmodeIc.setTextColor(all82);
                mViewBinding.workmodeTv.setTextColor(all82);
                mViewBinding.fanmodeIc.setTextColor(all82);
                mViewBinding.fanmodeTv.setTextColor(all82);
                mViewBinding.timingIc.setTextColor(all82);
                mViewBinding.timingTv.setTextColor(all82);
                int white2 = getResources().getColor(R.color.white2);
                mViewBinding.subTv.setTextColor(white2);
                mViewBinding.addTv.setTextColor(white2);
                mViewBinding.targetTemShowTv.setTextColor(white2);
                mViewBinding.targetTemTv.setTextColor(white2);
                break;
            }
            case 1: {
                // 打开
                int indexImgColor = getResources().getColor(R.color.index_imgcolor);
                mViewBinding.switchIc.setTextColor(indexImgColor);
                mViewBinding.switchTv.setTextColor(indexImgColor);
                mViewBinding.workmodeIc.setTextColor(indexImgColor);
                mViewBinding.workmodeTv.setTextColor(indexImgColor);
                mViewBinding.fanmodeIc.setTextColor(indexImgColor);
                mViewBinding.fanmodeTv.setTextColor(indexImgColor);
                mViewBinding.timingIc.setTextColor(indexImgColor);
                mViewBinding.timingTv.setTextColor(indexImgColor);
                int white = getResources().getColor(R.color.white);
                mViewBinding.subTv.setTextColor(white);
                mViewBinding.addTv.setTextColor(white);
                mViewBinding.targetTemShowTv.setTextColor(white);
                mViewBinding.targetTemTv.setTextColor(white);
                break;
            }
        }
    }
}