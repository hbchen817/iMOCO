package com.rexense.smart.view;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.rexense.smart.R;
import com.rexense.smart.contract.CTSL;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityNewAirForFullScreenBinding;
import com.rexense.smart.model.ETSL;
import com.rexense.smart.presenter.PluginHelper;
import com.rexense.smart.presenter.TSLHelper;

public class NewAirForFSSActivity extends DetailActivity {
    private ActivityNewAirForFullScreenBinding mViewBinding;

    private int mFanMode = 0;//0: 低风 1: 中风 2: 高风
    private int mPowerSwitch = 3;// 3: 关闭 4: 打开

    private TSLHelper mTSLHelper;

    private int mShapeCornerAppGreen;
    private int mAppGreen;
    private int mShapeCorner;
    private int mIndexImgColor;
    private int mShapeCornerAppGreen2;
    private int mAppGreen2;
    private int mAll82;

    // 更新状态
    protected boolean updateState(ETSL.propertyEntry propertyEntry) {
        if (propertyEntry == null || propertyEntry.properties == null || propertyEntry.properties.size() == 0) {
            return false;
        }

        // 风速
        if (propertyEntry.getPropertyValue(CTSL.FSS_WindSpeed_2) != null && propertyEntry.getPropertyValue(CTSL.FSS_WindSpeed_2).length() > 0) {
            String windSpeed = propertyEntry.getPropertyValue(CTSL.FSS_WindSpeed_2);
            mFanMode = Integer.parseInt(windSpeed) - 2;
            refreshFanLayout(mFanMode);
        }

        // 开关
        if (propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_2) != null && propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_2).length() > 0) {
            String powerSwitch = propertyEntry.getPropertyValue(CTSL.FSS_PowerSwitch_2);
            mPowerSwitch = Integer.parseInt(powerSwitch) + 3;
            refreshFanLayout(mPowerSwitch);
        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityNewAirForFullScreenBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        initStatusBar();
        initView();
        mTSLHelper = new TSLHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewBinding.includeToolbar.includeDetailImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mViewBinding.includeToolbar.includeDetailLblTitle.setText(R.string.new_air);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mViewBinding.includeToolbar.includeDetailRl.setBackgroundColor(Color.WHITE);
        mViewBinding.includeToolbar.includeDetailImgMore.setVisibility(View.GONE);
    }

    private void initView() {
        Typeface iconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        mViewBinding.lowWindIc.setTypeface(iconfont);
        mViewBinding.midWindIc.setTypeface(iconfont);
        mViewBinding.highWindIc.setTypeface(iconfont);
        mViewBinding.switchIc.setTypeface(iconfont);
        mViewBinding.timingIc.setTypeface(iconfont);

        mViewBinding.lowWindLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.midWindLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.highWindLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.switchLayout.setOnClickListener(this::onViewClicked);
        mViewBinding.timingLayout.setOnClickListener(this::onViewClicked);

        mShapeCornerAppGreen = R.drawable.shape_corner_appgreen;
        mAppGreen = getResources().getColor(R.color.appgreen);
        mShapeCorner = R.drawable.shape_corner;
        mIndexImgColor = getResources().getColor(R.color.index_imgcolor);
        mShapeCornerAppGreen2 = R.drawable.shape_corner_appgreen_2;
        mAppGreen2 = getResources().getColor(R.color.appgreen_2);
        mAll82 = getResources().getColor(R.color.all_8_2);
    }

    protected void onViewClicked(View view) {
        if (view.getId() == R.id.low_wind_layout) {
            // 低风
            if (mPowerSwitch == 4)
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_WindSpeed_2}, new String[]{"" + 2});
        } else if (view.getId() == R.id.mid_wind_layout) {
            // 中风
            if (mPowerSwitch == 4)
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_WindSpeed_2}, new String[]{"" + 3});
        } else if (view.getId() == R.id.high_wind_layout) {
            // 高风
            if (mPowerSwitch == 4)
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_WindSpeed_2}, new String[]{"" + 4});
        } else if (view.getId() == R.id.switch_layout) {
            // 开关
            if (mPowerSwitch == 4) {
                // 关闭
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_PowerSwitch_2}, new String[]{"" + CTSL.STATUS_OFF});
            } else {
                // 打开
                mTSLHelper.setProperty(mIOTId, mProductKey, new String[]{CTSL.FSS_PowerSwitch_2}, new String[]{"" + CTSL.STATUS_ON});
            }
        } else if (view.getId() == R.id.timing_layout) {
            // 定时
            if (mPowerSwitch == 4)
                PluginHelper.cloudTimer(this, mIOTId, mProductKey);
        }
    }

    private void refreshFanLayout(int flag) {
        switch (flag) {
            case 0: {
                // 低风
                if (mPowerSwitch == 4) {
                    mViewBinding.lowWindLayout.setBackgroundResource(mShapeCornerAppGreen);
                    mViewBinding.lowWindIc.setTextColor(mAppGreen);
                    mViewBinding.lowWindTv.setTextColor(mAppGreen);

                    mViewBinding.midWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.midWindIc.setTextColor(mIndexImgColor);
                    mViewBinding.midWindTv.setTextColor(mIndexImgColor);

                    mViewBinding.highWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.highWindIc.setTextColor(mIndexImgColor);
                    mViewBinding.highWindTv.setTextColor(mIndexImgColor);
                } else if (mPowerSwitch == 3) {
                    mViewBinding.lowWindLayout.setBackgroundResource(mShapeCornerAppGreen2);
                    mViewBinding.lowWindIc.setTextColor(mAppGreen2);
                    mViewBinding.lowWindTv.setTextColor(mAppGreen2);

                    mViewBinding.midWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.midWindIc.setTextColor(mAll82);
                    mViewBinding.midWindTv.setTextColor(mAll82);

                    mViewBinding.highWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.highWindIc.setTextColor(mAll82);
                    mViewBinding.highWindTv.setTextColor(mAll82);
                }

                mViewBinding.fanmodeShowTv.setText(getString(R.string.fan_speed_low));
                if (mPowerSwitch == 3) break;
                mViewBinding.fanmodeShowTv.setTextColor(mAppGreen);
                break;
            }
            case 1: {
                // 中风
                if (mPowerSwitch == 4) {
                    mViewBinding.lowWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.lowWindIc.setTextColor(mIndexImgColor);
                    mViewBinding.lowWindTv.setTextColor(mIndexImgColor);

                    mViewBinding.midWindLayout.setBackgroundResource(mShapeCornerAppGreen);
                    mViewBinding.midWindIc.setTextColor(mAppGreen);
                    mViewBinding.midWindTv.setTextColor(mAppGreen);

                    mViewBinding.highWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.highWindIc.setTextColor(mIndexImgColor);
                    mViewBinding.highWindTv.setTextColor(mIndexImgColor);
                } else if (mPowerSwitch == 3) {
                    mViewBinding.lowWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.lowWindIc.setTextColor(mAll82);
                    mViewBinding.lowWindTv.setTextColor(mAll82);

                    mViewBinding.midWindLayout.setBackgroundResource(mShapeCornerAppGreen2);
                    mViewBinding.midWindIc.setTextColor(mAppGreen2);
                    mViewBinding.midWindTv.setTextColor(mAppGreen2);

                    mViewBinding.highWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.highWindIc.setTextColor(mAll82);
                    mViewBinding.highWindTv.setTextColor(mAll82);
                }

                mViewBinding.fanmodeShowTv.setText(getString(R.string.fan_speed_mid));
                if (mPowerSwitch == 3) break;
                mViewBinding.fanmodeShowTv.setTextColor(mAppGreen);
                break;
            }
            case 2: {
                // 高风
                if (mPowerSwitch == 4) {
                    mViewBinding.lowWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.lowWindIc.setTextColor(mIndexImgColor);
                    mViewBinding.lowWindTv.setTextColor(mIndexImgColor);

                    mViewBinding.midWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.midWindIc.setTextColor(mIndexImgColor);
                    mViewBinding.midWindTv.setTextColor(mIndexImgColor);

                    mViewBinding.highWindLayout.setBackgroundResource(mShapeCornerAppGreen);
                    mViewBinding.highWindIc.setTextColor(mAppGreen);
                    mViewBinding.highWindTv.setTextColor(mAppGreen);
                } else if (mPowerSwitch == 3) {
                    mViewBinding.lowWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.lowWindIc.setTextColor(mAll82);
                    mViewBinding.lowWindTv.setTextColor(mAll82);

                    mViewBinding.midWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.midWindIc.setTextColor(mAll82);
                    mViewBinding.midWindTv.setTextColor(mAll82);

                    mViewBinding.highWindLayout.setBackgroundResource(mShapeCornerAppGreen2);
                    mViewBinding.highWindIc.setTextColor(mAppGreen2);
                    mViewBinding.highWindTv.setTextColor(mAppGreen2);
                }

                mViewBinding.fanmodeShowTv.setText(getString(R.string.fan_speed_high));
                if (mPowerSwitch == 3) break;
                mViewBinding.fanmodeShowTv.setTextColor(mAppGreen);
                break;
            }
            case 3: {
                // 关闭
                mViewBinding.switchIc.setTextColor(mAll82);
                mViewBinding.switchTv.setTextColor(mAll82);
                mViewBinding.timingIc.setTextColor(mAll82);
                mViewBinding.timingTv.setTextColor(mAll82);

                if (mFanMode == 0) {
                    mViewBinding.lowWindLayout.setBackgroundResource(mShapeCornerAppGreen2);
                    mViewBinding.lowWindIc.setTextColor(mAppGreen2);
                    mViewBinding.lowWindTv.setTextColor(mAppGreen2);
                } else {
                    mViewBinding.lowWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.lowWindIc.setTextColor(mAll82);
                    mViewBinding.lowWindTv.setTextColor(mAll82);
                }

                if (mFanMode == 1) {
                    mViewBinding.midWindLayout.setBackgroundResource(mShapeCornerAppGreen2);
                    mViewBinding.midWindIc.setTextColor(mAppGreen2);
                    mViewBinding.midWindTv.setTextColor(mAppGreen2);
                } else {
                    mViewBinding.midWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.midWindIc.setTextColor(mAll82);
                    mViewBinding.midWindTv.setTextColor(mAll82);
                }

                if (mFanMode == 2) {
                    mViewBinding.highWindLayout.setBackgroundResource(mShapeCornerAppGreen2);
                    mViewBinding.highWindIc.setTextColor(mAppGreen2);
                    mViewBinding.highWindTv.setTextColor(mAppGreen2);
                } else {
                    mViewBinding.highWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.highWindIc.setTextColor(mAll82);
                    mViewBinding.highWindTv.setTextColor(mAll82);
                }

                mViewBinding.fanmodeShowTv.setTextColor(mAppGreen2);
                break;
            }

            case 4: {
                // 打开
                mViewBinding.switchIc.setTextColor(mIndexImgColor);
                mViewBinding.switchTv.setTextColor(mIndexImgColor);
                mViewBinding.timingIc.setTextColor(mIndexImgColor);
                mViewBinding.timingTv.setTextColor(mIndexImgColor);

                if (mFanMode == 0) {
                    mViewBinding.lowWindLayout.setBackgroundResource(mShapeCornerAppGreen);
                    mViewBinding.lowWindIc.setTextColor(mAppGreen);
                    mViewBinding.lowWindTv.setTextColor(mAppGreen);
                } else {
                    mViewBinding.lowWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.lowWindIc.setTextColor(mIndexImgColor);
                    mViewBinding.lowWindTv.setTextColor(mIndexImgColor);
                }

                if (mFanMode == 1) {
                    mViewBinding.midWindLayout.setBackgroundResource(mShapeCornerAppGreen);
                    mViewBinding.midWindIc.setTextColor(mAppGreen);
                    mViewBinding.midWindTv.setTextColor(mAppGreen);
                } else {
                    mViewBinding.midWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.midWindIc.setTextColor(mIndexImgColor);
                    mViewBinding.midWindTv.setTextColor(mIndexImgColor);
                }

                if (mFanMode == 2) {
                    mViewBinding.highWindLayout.setBackgroundResource(mShapeCornerAppGreen);
                    mViewBinding.highWindIc.setTextColor(mAppGreen);
                    mViewBinding.highWindTv.setTextColor(mAppGreen);
                } else {
                    mViewBinding.highWindLayout.setBackgroundResource(mShapeCorner);
                    mViewBinding.highWindIc.setTextColor(mIndexImgColor);
                    mViewBinding.highWindTv.setTextColor(mIndexImgColor);
                }
                mViewBinding.fanmodeShowTv.setTextColor(mAppGreen);
                break;
            }
        }
    }
}